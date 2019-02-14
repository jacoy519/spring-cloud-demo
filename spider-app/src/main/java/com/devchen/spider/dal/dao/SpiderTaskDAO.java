package com.devchen.spider.dal.dao;

import com.devchen.spider.dal.entity.SpiderTaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface SpiderTaskDAO {

    int insertOne(@Param("spiderTask") SpiderTaskEntity spiderTask);

    int updateStatus(@Param("spiderTask") SpiderTaskEntity spiderTask);

    int updateUpdatedAt(@Param("id") long id);

    int updateStatusWithoutVersion(@Param("spiderTask") SpiderTaskEntity spiderTask);


    List<SpiderTaskEntity> selectByTaskTypeAndTaskJobAndStatus(@Param("taskType") String taskType,
                                                               @Param("taskJob") String taskJob,
                                                               @Param("taskStatusList") List<String> taskStatusList);


    List<SpiderTaskEntity> selectBySpiderIdAndTaskStatusAndTaskJob(@Param("taskJob") String taskType,
                                                               @Param("spiderId") String spiderId,
                                                               @Param("taskStatusList") List<String> taskStatusList);


    List<SpiderTaskEntity> selectByTaskTypeAndTaskTimeAndTaskJobAndStatusList(
            @Param("taskTime") Date taskTime,
            @Param("taskJob") String taskJob,
            @Param("taskType") String taskType,
            @Param("taskStatusList") List<String> taskStatusList);

    SpiderTaskEntity selectOneById(@Param("id") long id);


}
