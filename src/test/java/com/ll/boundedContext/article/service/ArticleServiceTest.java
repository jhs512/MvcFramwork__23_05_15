package com.ll.boundedContext.article.service;

import com.ll.boundedContext.article.entity.Article;
import com.ll.framework.Container;
import com.ll.myMap.MyMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ArticleServiceTest {
    private MyMap myMap;
    private ArticleService articleService;
    private static final int TEST_DATA_SIZE = 100;

    public ArticleServiceTest() {
        myMap = Container.getObj(MyMap.class);
        articleService = Container.getObj(ArticleService.class);
    }

    // @BeforeAll 붙인 아래 메서드는
    @BeforeAll
    public void BeforeAll() {
        // 모든 DB 처리시에, 처리되는 SQL을 콘솔에 출력
        myMap.setDevMode(true);
    }

    // @BeforeEach를 붙인 아래 메서드는
    // @Test가 달려있는 메서드가 실행되기 전에 자동으로 실행이 된다.
    // 주로 테스트 환경을 깔끔하게 정리하는 역할을 한다.
    // 즉 각각의 테스트케이스가 독립적인 환경에서 실행될 수 있도록 하는 역할을 한다.
    @BeforeEach
    public void beforeEach() {
        // 게시물 테이블을 깔끔하게 삭제한다.
        // DELETE FROM article; // 보다 TRUNCATE article; 로 삭제하는게 더 깔끔하고 흔적이 남지 않는다.
        truncateArticleTable();
        // 게시물 3개를 만든다.
        // 테스트에 필요한 샘플데이터를 만든다고 보면 된다.
        makeArticleTestData();
    }

    private void makeArticleTestData() {
        IntStream.rangeClosed(1, TEST_DATA_SIZE).forEach(no -> {
            boolean isBlind = no >= 11 && no <= 20;
            String title = "제목%d".formatted(no);
            String body = "내용%d".formatted(no);

            myMap.run("""
                    INSERT INTO article
                    SET createdDate = NOW(),
                    modifiedDate = NOW(),
                    title = ?,
                    `body` = ?,
                    isBlind = ?
                    """, title, body, isBlind);
        });
    }

    private void truncateArticleTable() {
        // 테이블을 깔끔하게 지워준다.
        myMap.run("TRUNCATE article");
    }

    @Test
    public void 존재한다() {
        assertThat(articleService).isNotNull();
    }

    @Test
    public void getArticles() {
        List<Article> articleDtoList = articleService.getArticles();
        assertThat(articleDtoList.size()).isEqualTo(TEST_DATA_SIZE);
    }

    @Test
    public void getArticleById() {
        Article articleDto = articleService.getArticleById(1);

        assertThat(articleDto.getId()).isEqualTo(1L);
        assertThat(articleDto.getTitle()).isEqualTo("제목1");
        assertThat(articleDto.getBody()).isEqualTo("내용1");
        assertThat(articleDto.getCreatedDate()).isNotNull();
        assertThat(articleDto.getModifiedDate()).isNotNull();
        assertThat(articleDto.isBlind()).isFalse();
    }

    @Test
    public void getArticlesCount() {
        long articlesCount = articleService.getArticlesCount();

        assertThat(articlesCount).isEqualTo(TEST_DATA_SIZE);
    }

    @Test
    public void write() {
        long newArticleId = articleService.write("제목 new", "내용 new", false);

        Article articleDto = articleService.getArticleById(newArticleId);

        assertThat(articleDto.getId()).isEqualTo(newArticleId);
        assertThat(articleDto.getTitle()).isEqualTo("제목 new");
        assertThat(articleDto.getBody()).isEqualTo("내용 new");
        assertThat(articleDto.getCreatedDate()).isNotNull();
        assertThat(articleDto.getModifiedDate()).isNotNull();
        assertThat(articleDto.isBlind()).isEqualTo(false);
    }

    @Test
    public void modify() {
        //Ut.sleep(5000);

        articleService.modify(1, "제목 new", "내용 new", true);

        Article articleDto = articleService.getArticleById(1);

        assertThat(articleDto.getId()).isEqualTo(1);
        assertThat(articleDto.getTitle()).isEqualTo("제목 new");
        assertThat(articleDto.getBody()).isEqualTo("내용 new");
        assertThat(articleDto.isBlind()).isEqualTo(true);

        // DB에서 받아온 게시물 수정날짜와 자바에서 계산한 현재 날짜를 비교하여(초단위)
        // 그것이 1초 이하로 차이가 난다면
        // 수정날짜가 갱신되었다 라고 볼 수 있음
        long diffSeconds = ChronoUnit.SECONDS.between(articleDto.getModifiedDate(), LocalDateTime.now());
        assertThat(diffSeconds).isLessThanOrEqualTo(1L);
    }

    @Test
    public void delete() {
        articleService.delete(1);

        Article articleDto = articleService.getArticleById(1);

        assertThat(articleDto).isNull();
    }

    @Test
    public void _2번글의_이전글은_1번글_이다() {
        Article id2Article = articleService.getArticleById(2);
        Article id1Article = articleService.getPrevArticle(id2Article);

        assertThat(id1Article.getId()).isEqualTo(1);
    }

    @Test
    public void _1번글의_이전글은_없다() {
        Article id1Article = articleService.getArticleById(1);
        Article nullArticle = articleService.getPrevArticle(id1Article);

        assertThat(nullArticle).isNull();
    }

    @Test
    public void _2번글의_다음글은_3번글_이다() {
        Article id3Article = articleService.getNextArticle(2);

        assertThat(id3Article.getId()).isEqualTo(3);
    }

    @Test
    public void 마지막글의_다음글은_없다() {
        long lastArticleId = TEST_DATA_SIZE;
        Article nullArticle = articleService.getNextArticle(lastArticleId);

        assertThat(nullArticle).isNull();
    }

    @Test
    public void _10번글의_다음글은_21번글_이다_왜냐하면_11번글부터_20번글까지는_블라인드라서() {
        Article nextArticle = articleService.getNextArticle(10);

        assertThat(nextArticle.getId()).isEqualTo(21);
    }
}