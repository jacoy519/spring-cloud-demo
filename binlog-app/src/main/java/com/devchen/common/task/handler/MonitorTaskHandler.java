package com.devchen.common.task.handler;

import com.devchen.common.task.constant.TaskJob;
import com.devchen.common.task.dto.AbstractTaskDTO;



public abstract class MonitorTaskHandler<T extends AbstractTaskDTO> implements TaskHandler<T> {

    public final TaskJob getTaskJob() {
        return TaskJob.MONITOR;
    }
}
