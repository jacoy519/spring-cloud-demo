package com.devchen.proxy.dal.dao;

import com.devchen.proxy.dal.entity.WeixinPageSourceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WeixinPageSourceDAO {

    int insertOne(@Param("page") WeixinPageSourceEntity page);

    int updatePageUrl(@Param("page") WeixinPageSourceEntity page);

    WeixinPageSourceEntity selectOne(@Param("weixinId") String weixinId);
}
