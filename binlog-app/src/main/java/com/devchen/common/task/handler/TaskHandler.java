package com.devchen.common.task.handler;

import com.devchen.common.task.constant.TaskJob;
import com.devchen.common.task.constant.TaskType;
import com.devchen.common.task.dto.AbstractTaskDTO;

public interface TaskHandler<T extends AbstractTaskDTO> {


    TaskType getTaskType();

    TaskJob getTaskJob();

    boolean isStatisfied(T taskEntity);

    void beforeTask(T taskEntity);

    boolean doTask(T taskEntity);

    void afterTask(T taskEntity);
}
