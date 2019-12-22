package com.devchen.common.task.handler;

import com.devchen.common.task.constant.TaskJob;
import com.devchen.common.task.constant.TaskType;
import com.devchen.common.task.dto.AbstractTaskDTO;
import com.devchen.spider.dal.entity.SpiderTaskEntity;
import org.apache.log4j.Logger;


public abstract class ExecutorTaskHandler<T extends AbstractTaskDTO> implements TaskHandler<T> {

    public final TaskJob getTaskJob() {
        return TaskJob.EXECUTOR;
    }
}
