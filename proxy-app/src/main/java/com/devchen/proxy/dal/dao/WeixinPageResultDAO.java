package com.devchen.proxy.dal.dao;

import com.devchen.proxy.dal.entity.WeixinPageResultEntity;
import com.devchen.proxy.dal.entity.WeixinPageSourceEntity;
import com.devchen.proxy.dal.entity.WeixinSpiderTargetEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WeixinPageResultDAO {

    int insertOne(@Param("page") WeixinPageResultEntity page);

    int updatePageUrl(@Param("page") WeixinPageResultEntity page);

    WeixinPageResultEntity selectOne(@Param("weixinId") String weixinId);
}
