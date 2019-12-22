package com.devchen.common.task.mgr;

import com.devchen.common.task.constant.TaskJob;
import com.devchen.common.task.constant.TaskStatus;
import com.devchen.common.task.constant.TaskType;
import com.devchen.common.task.dao.AbstractTaskDAO;
import com.devchen.common.task.dto.AbstractTaskDTO;
import com.devchen.common.task.endpoint.TaskWorkerEndPoint;
import com.devchen.common.task.handler.TaskHandler;
import com.devchen.common.task.loader.TaskHandlerFactory;
import com.devchen.spider.enums.SpiderTaskStatus;
import com.devchen.spider.service.common.SpiderConfigService;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.*;

public abstract class AbstractTaskManager<T extends AbstractTaskDTO> implements TaskManager {

    @Resource
    private SpiderConfigService spiderConfigService;

    @Resource
    private TaskWorkerEndPoint taskWorkerEndPoint;

    private final static long MAX_MONITOR_RUN_TIME = 100000L;

    private final static Logger logger = Logger.getLogger(AbstractTaskManager.class);

    protected abstract ThreadPoolTaskExecutor getMonitorTaskThreadPool();

    protected abstract ThreadPoolTaskExecutor getExecutorTaskThreadPool();

    protected abstract AbstractTaskDAO<T> getTaskDAO();

    protected abstract TaskHandlerFactory<T> getTaskHandlerFactory();


    public void run() {
        initTask();
        runMonitorTask();
    }


    protected abstract void initTask();

    private void monitorAllRunningTask() {
        TaskType taskType = getTaskType();
        List<T> taskList = queryRunningTask(taskType.name());
        for(T task : taskList) {

        }
    }

    private void doMonitorTask(T task) {
        if(!taskWorkerEndPoint.isWorkEndPointExist(task.getRunnerId())) {
            terminateTask(task);
            logger.info(String.format("[doMonitorTask] ternimate the task due to the task work end point not exist. task info [%s]",
                    (new Gson()).toJson(task)));
        }
    }



    private void runMonitorTask() {
        TaskType taskType = getTaskType();
        List<T> monitorTaskList = queryTask(taskType.name(), TaskJob.MONITOR.name());
        for(T monitorTask : monitorTaskList) {
            doSubmitTask(monitorTask);
        }
    }


    public final void submitExecutorTask(long taskId) {
        AbstractTaskDAO<T> taskDAO = getTaskDAO();
        T taskDTO = taskDAO.selectById(taskId);
        if(taskDTO == null) {
            logger.info(String.format("[submitExecutorTask] fail to submit task due to not read task, task id [%s]", String.valueOf(taskId)));
            return;
        }
        doSubmitTask(taskDTO);
    }

    private void doSubmitTask(T taskDTO) {
        ThreadPoolTaskExecutor executorThreadPool = getTaskThreadPool(taskDTO);
        if(executorThreadPool == null) {
            logger.warn(String.format("[doSubmitTask] fail to submit task due to not get thread pool executor. the task is [%s]",
                    (new Gson()).toJson(taskDTO)));
        }

        try {
            executorThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    if(lockTask(taskDTO)) {
                        TaskHandler<T> taskHandler = getTaskHandler(taskDTO);
                        if(taskHandler != null) {
                            logger.info(String.format("[doSubmitTask] start to exec task. task info [%s]",
                                    (new Gson()).toJson(taskDTO)));
                            boolean isRunSuccess = false;
                            try {
                                taskHandler.beforeTask(taskDTO);
                                isRunSuccess = taskHandler.doTask(taskDTO);
                                taskHandler.afterTask(taskDTO);
                            } catch (Exception e) {
                                logger.error(String.format("[doSubmitTask] fail to exec task due to exception. task info [%s]",
                                        (new Gson()).toJson(taskDTO)), e);
                            }
                            if(isRunSuccess) {
                                logger.info(String.format("[doSubmitTask] end to exec task. task info [%s]",
                                        (new Gson()).toJson(taskDTO)));
                                finishTask(taskDTO);
                            } else {
                                logger.info(String.format("[doSubmitTask] end to exec task and task fail. task info [%s]",
                                        (new Gson()).toJson(taskDTO)));
                                unlockTask(taskDTO);
                            }
                        } else {
                            logger.warn(String.format("[doSubmitTask] fail to exec task due to not find task handler. task info [%s]",
                                    (new Gson()).toJson(taskDTO)));
                            unlockTask(taskDTO);
                        }

                    }
                }
            });
        } catch (Exception e) {
            logger.warn(String.format("[doSubmitTask] fail to submit task due to execptionr. the task is [%s]",
                    (new Gson()).toJson(taskDTO)),e);
        }

    }

    private TaskHandler<T> getTaskHandler(T taskDTO) {
        TaskHandlerFactory<T> taskHandlerFactory = getTaskHandlerFactory();
        return taskHandlerFactory.getTaskHandler(taskDTO);
    }


    private ThreadPoolTaskExecutor getTaskThreadPool(T taskDTO) {
        if(TaskJob.EXECUTOR.name().equals(taskDTO.getTaskJob())) {
            return getExecutorTaskThreadPool();
        }
        if(TaskJob.MONITOR.name().equals(taskDTO.getTaskJob())) {
            return getMonitorTaskThreadPool();
        }
        return null;
    }


    private List<T> queryTask(String taskType, String taskJob) {
        List<String> taskStatus = new ArrayList<>();
        taskStatus.add(SpiderTaskStatus.NEW.toString());

        List<String> taskJobList = new ArrayList<>();
        taskJobList.add(taskJob);

        return  getTaskDAO().selectByTaskTypeAndTaskJobAndStatus(taskType, taskJobList, taskStatus);
    }

    private List<T> queryRunningTask(String taskType) {
        List<String> taskStatus = new ArrayList<>();
        taskStatus.add(SpiderTaskStatus.NEW.toString());

        List<String> taskJobList = new ArrayList<>();
        taskJobList.add(TaskJob.EXECUTOR.name());
        taskJobList.add(TaskJob.MONITOR.name());


        return  getTaskDAO().selectByTaskTypeAndTaskJobAndStatus(taskType, taskJobList, taskStatus);
    }

    private void terminateTask(T taskDTO) {
        updateTaskStatus(taskDTO, TaskStatus.TERMINATED);
    }

    private void finishTask(T taskDTO) {
        updateTaskStatus(taskDTO, TaskStatus.SUCCESS);
    }

    private boolean lockTask(T taskDTO) {
        return updateTaskStatus(taskDTO, TaskStatus.PROCESSING);
    }

    private boolean unlockTask(T taskDTO) {
        return updateTaskStatus(taskDTO, TaskStatus.NEW);
    }

    private boolean updateTaskStatus(T taskDTO, TaskStatus nextStatus) {
        AbstractTaskDAO<T> taskDAO = getTaskDAO();
        taskDTO.setTaskStatus(nextStatus.name());
        int updateNum = taskDAO.updateStatus(taskDTO);
        if(updateNum == 1) {
            taskDTO.setVersion(taskDTO.getVersion() + 1L);
            return true;
        }
        return false;
    }

}
