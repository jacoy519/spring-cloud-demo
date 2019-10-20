CREATE TABLE proxy_config (
  id bigint unsigned auto_increment,
  created_at datetime not null ,
  created_by varchar(30) not null default 'SYS',
  updated_at datetime not null ,
  updated_by varchar(30) not null default 'SYS',
  key_name varchar(128) NOT NULL,
  key_value varchar(4000) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8;


insert into proxy_config(created_at,created_by,updated_at,updated_by,key_name,key_value)
value (now(),'sys', now(), 'sys', 'zod_game',
'__cfduid=dec7b9b88aad3d3b78180263124561c9e1571451304; qhMq_2132_saltkey=jLN6j6XM; qhMq_2132_lastvisit=1571447704; qhMq_2132_auth=ad81ghj5z3qWfI70LL%2B6M485xDvknfjoBGXjxaL2FTKBFO10SiCpDLmzOU0TO4u0%2FY287NDECNjIOfhlIvS9QugHIp8; qhMq_2132_lastcheckfeed=593889%7C1571451320; qhMq_2132_myrepeat_rr=R0; qhMq_2132_smile=4D1; qhMq_2132_nofavfid=1; qhMq_2132_sid=yq8qz3; qhMq_2132_lip=101.87.138.119%2C1571454676; PHPSESSID=oef5denr249243dd4vstrdmc1j; qhMq_2132_ulastactivity=21f7jtd8hAht992MZIXUopGPVWBbFlENpfjtTOZlTsjTGIi9UVTI; qhMq_2132_sendmail=1; qhMq_2132_noticeTitle=1; qhMq_2132_onlineusernum=377; qhMq_2132_st_t=593889%7C1571478739%7C28bc3a6e800467d18d60d9915c97786f; qhMq_2132_forum_lastvisit=D_75_1571454676D_16_1571478739; qhMq_2132_st_p=593889%7C1571478750%7Ceb28569eba40728fc5bfaaa3f640c7a2; qhMq_2132_viewid=tid_188916; qhMq_2132_lastact=1571478797%09misc.php%09patch');

