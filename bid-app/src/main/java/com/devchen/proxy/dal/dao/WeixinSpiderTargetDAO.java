package com.devchen.proxy.dal.dao;

import com.devchen.proxy.dal.entity.WeixinSpiderTargetEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WeixinSpiderTargetDAO {

    List<WeixinSpiderTargetEntity> selectAll();
}
