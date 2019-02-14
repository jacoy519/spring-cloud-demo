CREATE TABLE spider_task_config (
  id bigint unsigned auto_increment,
  created_at datetime not null ,
  created_by varchar(30) not null default 'SYS',
  updated_at datetime not null ,
  updated_by varchar(30) not null default 'SYS',
  group_name varchar(128) NOT NULL,
  key_name varchar(128) NOT NULL,
  key_value varchar(128) NOT NULL,
  is_valid varchar(30) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8;
