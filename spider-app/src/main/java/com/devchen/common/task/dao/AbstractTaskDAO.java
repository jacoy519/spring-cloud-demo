package com.devchen.common.task.dao;

import com.devchen.common.task.dto.AbstractTaskDTO;

import java.util.List;

public interface AbstractTaskDAO<T extends AbstractTaskDTO> {

    int updateStatus(T taskEntity);

    int forceUpdateStatus(T taskEntity);

    List<T> queryTaskList(List<String> taskTypeList, List<String> taskJobList, List<String> taskStatusList);

    T selectById(long taskId);
}
