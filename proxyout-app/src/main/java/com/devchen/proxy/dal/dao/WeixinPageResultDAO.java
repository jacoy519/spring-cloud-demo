package com.devchen.proxy.dal.dao;

import com.devchen.proxy.dal.entity.WeixinPageResultEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WeixinPageResultDAO {

    int insertOne(@Param("page") WeixinPageResultEntity page);

    int updatePageUrl(@Param("page") WeixinPageResultEntity page);

    WeixinPageResultEntity selectOne(@Param("weixinId") String weixinId);
}
