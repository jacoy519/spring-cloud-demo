package com.devchen.common.task.handler;

import com.devchen.common.task.constant.TaskJob;
import com.devchen.common.task.constant.TaskType;
import com.devchen.common.task.dto.AbstractTaskDTO;


public class MonitorTaskHandler<T extends AbstractTaskDTO> implements TaskHandler<T> {

    @Override
    public TaskType getTaskType() {
        return null;
    }

    public final TaskJob getTaskJob() {
        return TaskJob.MONITOR;
    }

    @Override
    public boolean isStatisfied(T taskEntity) {
        return false;
    }

    @Override
    public void beforeTask(T taskEntity) {

    }

    @Override
    public boolean doTask(T taskEntity) {
        return false;
    }

    @Override
    public void afterTask(T taskEntity) {

    }
}
