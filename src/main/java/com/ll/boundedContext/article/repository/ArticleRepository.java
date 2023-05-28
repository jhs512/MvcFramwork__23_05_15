package com.ll.boundedContext.article.repository;


import com.ll.boundedContext.article.entity.Article;
import com.ll.framework.annotation.Autowired;
import com.ll.framework.annotation.Repository;
import com.ll.myMap.MyMap;
import com.ll.myMap.SecSql;

import java.util.List;

@Repository
public class ArticleRepository {
    @Autowired
    private MyMap myMap;

    public List<Article> getArticles() {
        SecSql sql = myMap.genSecSql();
        sql
                .append("SELECT *")
                .append("FROM article")
                .append("ORDER BY id DESC");
        return sql.selectRows(Article.class);
    }

    public Article getArticleById(long id) {
        SecSql sql = myMap.genSecSql();
        sql
                .append("SELECT *")
                .append("FROM article")
                .append("WHERE id = ?", id);
        return sql.selectRow(Article.class);
    }

    public long getArticlesCount() {
        SecSql sql = myMap.genSecSql();
        sql
                .append("SELECT COUNT(*)")
                .append("FROM article");
        return sql.selectLong();
    }

    public long write(String title, String body, boolean isBlind) {
        SecSql sql = myMap.genSecSql();
        sql
                .append("INSERT INTO article")
                .append("SET createdDate = NOW()")
                .append(", modifiedDate = NOW()")
                .append(", title = ?", title)
                .append(", body = ?", body)
                .append(", isBlind = ?", isBlind);

        return sql.insert();
    }

    public void modify(long id, String title, String body, boolean isBlind) {
        SecSql sql = myMap.genSecSql();
        sql
                .append("UPDATE article")
                .append("SET modifiedDate = NOW()")
                .append(", title = ?", title)
                .append(", body = ?", body)
                .append(", isBlind = ?", isBlind)
                .append("WHERE id = ?", id);

        sql.update();
    }

    public void delete(long id) {
        SecSql sql = myMap.genSecSql();
        sql
                .append("DELETE FROM article")
                .append("WHERE id = ?", id);

        sql.update();
    }

    public Article getPrevArticle(long id) {
        SecSql sql = myMap.genSecSql();
        sql
                .append("SELECT *")
                .append("FROM article")
                .append("WHERE id < ?", id)
                .append("AND isBlind = 0")
                .append("ORDER BY id DESC")
                .append("LIMIT 1");
        return sql.selectRow(Article.class);
    }

    public Article getNextArticle(long id) {
        SecSql sql = myMap.genSecSql();
        sql
                .append("SELECT *")
                .append("FROM article")
                .append("WHERE id > ?", id)
                .append("AND isBlind = 0")
                .append("ORDER BY id ASC")
                .append("LIMIT 1");
        return sql.selectRow(Article.class);
    }
}
