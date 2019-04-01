CREATE TABLE weixin_page_source (
  id bigint unsigned auto_increment,
  created_at datetime not null ,
  created_by varchar(30) not null default 'SYS',
  updated_at datetime not null ,
  updated_by varchar(30) not null default 'SYS',
  weixin_id varchar(128) NOT NULL,
  page_url varchar(256) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8;
