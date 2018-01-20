CREATE TABLE t_download_task (
  id bigint unsigned auto_increment,
  remote_address varchar(1000) NOT NULL,
  local_save_dir varchar(1000) NOT NULL,
  download_type varchar(1000) NOT NULL,
  download_status varchar(30) NOT NULL,
  task_id varchar(1000) NOT NULL,
  created_at datetime not null ,
  created_by varchar(30) not null default 'SYS',
  updated_at datetime not null ,
  updated_by varchar(30) not null default 'SYS',
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8;
