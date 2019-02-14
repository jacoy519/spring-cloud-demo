CREATE TABLE msg_record (
  id bigint unsigned auto_increment,
  msg_content varchar(4000) NOT NULL,
  msg_send_type varchar(100) NOT NULL,
  dest varchar(100) NOT NULL,
  status varchar(100) NOT NULL,
  task_id INT NOT NULL,
  version INT NOT NULL,
  created_at datetime not null ,
  created_by varchar(30) not null default 'SYS',
  updated_at datetime not null ,
  updated_by varchar(30) not null default 'SYS',
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8;
