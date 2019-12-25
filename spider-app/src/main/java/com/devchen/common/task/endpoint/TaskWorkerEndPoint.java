package com.devchen.common.task.endpoint;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TaskWorkerEndPoint {

    private String id = UUID.randomUUID().toString();

    public void registTask(String taskType, long taskId) {

    }

    public void unregistTask(String taskType ,long taskId) {

    }

    public void submitTask(String taskType, String taskJob, long taskId) {

    }

    public boolean isWorkEndPointExist(String runId) {
        return true;
    }

    public String getId() {
        return id;
    }
}
