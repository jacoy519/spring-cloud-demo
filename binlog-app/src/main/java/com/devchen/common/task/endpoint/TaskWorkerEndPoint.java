package com.devchen.common.task.endpoint;

import org.springframework.stereotype.Component;

@Component
public class TaskWorkerEndPoint {

    public void registTask(String taskType, long taskId) {

    }

    public void unregistTask(String taskType ,long taskId) {

    }

    public boolean isWorkEndPointExist(String runId) {
        return true;
    }

}
