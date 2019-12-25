package com.devchen.common.task.mgr;

import com.devchen.common.task.constant.TaskType;

public interface TaskManager {


    void run();

    void submitExecutorTask(long executorTaskId);

}
