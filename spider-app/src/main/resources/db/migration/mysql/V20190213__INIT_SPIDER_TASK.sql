CREATE TABLE spider_task (
  id bigint unsigned auto_increment,
  created_at datetime not null ,
  created_by varchar(30) not null default 'SYS',
  updated_at datetime not null ,
  updated_by varchar(30) not null default 'SYS',
  task_type varchar(128) NOT NULL,
  spider_id varchar(128) NOT NULL,
  task_job varchar(32) NOT NULL,
  task_status varchar(30) NOT NULL,
  task_time datetime not null ,
  version bigint NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8;
