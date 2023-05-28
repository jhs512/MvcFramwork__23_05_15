# DB 생성
DROP DATABASE IF EXISTS mvc__dev;
CREATE DATABASE mvc__dev;
USE mvc__dev;

# article 테이블 생성
CREATE TABLE article (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    PRIMARY KEY(id),
    createdDate DATETIME NOT NULL,
    modifiedDate DATETIME NOT NULL,
    title VARCHAR(100) NOT NULL,
    `body` TEXT NOT NULL,
    isBlind BIT(1) NOT NULL DEFAULT(0)
);

# article 테이블 샘플 데이터 생성
INSERT INTO article
SET createdDate = NOW(),
modifiedDate = NOW(),
title = "제목1",
`body` = "내용1";

INSERT INTO article
SET createdDate = NOW(),
modifiedDate = NOW(),
title = "제목2",
`body` = "내용2";

INSERT INTO article
SET createdDate = NOW(),
modifiedDate = NOW(),
title = "제목3",
`body` = "내용3";