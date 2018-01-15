CREATE TABLE t_article (
  id bigint unsigned auto_increment,
  title varchar(255) NOT NULL,
  author varchar(255) NOT NULL,
  content mediumtext,
  article_type varchar(255) DEFAULT NULL,
  created_at datetime not null ,
  created_by varchar(30) not null default 'SYS',
  updated_at datetime not null ,
  updated_by varchar(30) not null default 'SYS',
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8;


insert into t_article(title, author, content, article_type, created_at, updated_at) select title, author, content, article_type, sysdate(), sysdate()  from article;