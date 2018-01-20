package com.devchen.file.dal.dao;

import com.devchen.file.dal.entity.DownloadTaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DownloadTaskDao {

    List<DownloadTaskEntity> selectTopAcceptArticle();

    int insertDownloadTask(@Param("task") DownloadTaskEntity task);

    int updateTaskDownloadStatusById(@Param("id") long id, @Param("status") String status);

    DownloadTaskEntity selectDownloadTaskByTaskId(@Param("taskId") String taskId);

}
