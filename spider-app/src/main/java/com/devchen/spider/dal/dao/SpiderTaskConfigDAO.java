package com.devchen.spider.dal.dao;

import com.devchen.spider.dal.entity.SpiderTaskConfigEntity;
import com.devchen.spider.dal.entity.SpiderTaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface SpiderTaskConfigDAO {




    SpiderTaskConfigEntity selectByGroupNameAndKeyName(@Param("groupName") String groupName,
                                                       @Param("keyName") String keyName);


}
