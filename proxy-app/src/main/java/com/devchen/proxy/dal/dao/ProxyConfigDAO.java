package com.devchen.proxy.dal.dao;

import com.devchen.proxy.dal.entity.ProxyConfigEntity;
import com.devchen.proxy.dal.entity.WeixinPageSourceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProxyConfigDAO {

    ProxyConfigEntity selectOne(@Param("keyName") String keyName);
}
