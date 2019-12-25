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
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTaskManager<T extends AbstractTaskDTO> implements TaskManager {

    @Resource
    private SpiderConfigService spiderConfigService;

    @Resource
    private TaskWorkerEndPoint taskWorkerEndPoint;

    private final static long MAX_MONITOR_RUN_TIME = 100000L;

    private final static Logger logger = Logger.getLogger(AbstractTaskManager.class);

    protected abstract AbstractTaskDAO<T> getTaskDAO();

    protected abstract TaskHandlerFactory<T> getTaskHandlerFactory();


    protected abstract ThreadPoolTaskExecutor getTaskThreadPool(T taskDTO);


    public void run() {
        monitorRunningTask();
        initNewTask();
        triggerNewTask();
    }

    protected void monitorRunningTask() {
        List<T> taskList = queryAllProcessingTask();
        for(T task : taskList) {
            if(!taskWorkerEndPoint.isWorkEndPointExist(task.getRunnerId())) {
                terminateTask(task);
            }
        }
    }

    protected abstract void initNewTask();

    protected void triggerNewTask() {
        List<T> taskList = queryAllNewTask();
        for(T task : taskList) {
            taskWorkerEndPoint.submitTask(task.getTaskType(), task.getTaskJob(), task.getId());
        }
    }

    private List<T> queryAllProcessingTask() {
        List<String> taskTypeList = new ArrayList<>();
        for(TaskType taskType : getTaskHandlerFactory().getTaskTypeSet()) {
            taskTypeList.add(taskType.name());
        }

        List<String> taskJobList = new ArrayList<>();
        for(TaskJob taskJob : TaskJob.values()) {
            taskJobList.add(taskJob.name());
        }

        List<String> taskStatusList = new ArrayList<>();
        taskStatusList.add(TaskStatus.PROCESSING.name());

        return getTaskDAO().queryTaskList(taskTypeList, taskJobList, taskStatusList);
    }

    private List<T> queryAllNewTask() {
        List<String> taskTypeList = new ArrayList<>();
        for(TaskType taskType : getTaskHandlerFactory().getTaskTypeSet()) {
            taskTypeList.add(taskType.name());
        }

        List<String> taskJobList = new ArrayList<>();
        for(TaskJob taskJob : TaskJob.values()) {
            taskJobList.add(taskJob.name());
        }

        List<String> taskStatusList = new ArrayList<>();
        taskStatusList.add(TaskStatus.NEW.name());

        return getTaskDAO().queryTaskList(taskTypeList, taskJobList, taskStatusList);
    }


    public final void submitExecutorTask(long taskId, String taskType, String taskJob) {
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



    private void terminateTask(T taskDTO) {
        forceUpdateTaskStatus(taskDTO, TaskStatus.TERMINATED);
    }

    private void finishTask(T taskDTO) {
        updateTaskStatus(taskDTO, TaskStatus.SUCCESS);
    }

    private boolean lockTask(T taskDTO) {
        taskDTO.setRunnerId(taskWorkerEndPoint.getId());
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

    private boolean forceUpdateTaskStatus(T taskDTO, TaskStatus nextStatus) {
        AbstractTaskDAO<T> taskDAO = getTaskDAO();
        taskDTO.setTaskStatus(nextStatus.name());
        int updateNum = taskDAO.forceUpdateStatus(taskDTO);
        if(updateNum == 1) {
            taskDTO.setVersion(taskDTO.getVersion() + 1L);
            return true;
        }
        return false;
    }

}
