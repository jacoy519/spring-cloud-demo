package com.devchen.common.task.dao;

import com.devchen.common.task.dto.AbstractTaskDTO;

import java.util.List;

public interface AbstractTaskDAO<T extends AbstractTaskDTO> {

    int updateStatus(T taskEntity);

    List<T> selectByTaskTypeAndTaskJobAndStatus(String taskType, List<String> taskJobList, List<String> taskStatusList);

    T selectById(long taskId);
}
