package com.devchen.spider.service.common.mgr;

import com.devchen.spider.dal.dao.SpiderTaskDAO;
import com.devchen.spider.dal.entity.SpiderTaskEntity;
import com.devchen.spider.enums.SpiderTaskJob;
import com.devchen.spider.enums.SpiderTaskStatus;
import com.devchen.spider.enums.SpiderTaskType;
import com.devchen.spider.enums.SpiderTimeModel;
import com.devchen.spider.service.common.SpiderConfigService;
import com.devchen.spider.service.common.handler.ITaskHandler;
import com.devchen.spider.service.common.handler.MonitorTaskHandler;
import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.*;

public class AbstractSpiderTaskManager {

    @Resource
    private SpiderTaskDAO spiderTaskDAO;

    @Resource
    private ThreadPoolTaskExecutor spiderExecutorPool;

    @Resource
    private ThreadPoolTaskExecutor spiderMoniterPool;

    @Resource
    private MonitorTaskHandler monitorTaskHandler;

    @Resource
    private SpiderConfigService spiderConfigService;


    private final static long MAX_MONITOR_RUN_TIME = 100000L;


    private final static Logger logger = Logger.getLogger(AbstractSpiderTaskManager.class);


    private Map<String, ITaskHandler> taskTypeMap = new HashMap<>();

    public void run() {
        initTask();
        submitTask();
    }


    private void initTask() {
        Set<String> taskTypeSet = taskTypeMap.keySet();
        for(String taskType : taskTypeSet) {
            String currentTaskType = taskType.split("%")[0];
            doInitTask(currentTaskType);
        }
    }

    private void doInitTask(String taskType) {
        List<String> status = new ArrayList<>();
        status.add(SpiderTaskStatus.NEW.name());
        status.add(SpiderTaskStatus.PROCESSING.name());
        List<SpiderTaskEntity> spiderTaskEntities = spiderTaskDAO.selectByTaskTypeAndTaskJobAndStatus(taskType, SpiderTaskJob.M.name(), status);
        if(!spiderTaskEntities.isEmpty()) {
            handleRunMonitor(spiderTaskEntities);
        } else {
            initNewTask(taskType);
        }

    }

    private void initNewTask(String taskType) {

        SpiderTimeModel timeModel = spiderConfigService.getSpiderTimeModel(SpiderTaskType.valueOf(taskType));
        Date taskTime = getTaskTime(timeModel);

        List<String> status = new ArrayList<>();
        status.add(SpiderTaskStatus.NEW.name());
        status.add(SpiderTaskStatus.PROCESSING.name());
        status.add(SpiderTaskStatus.SUCCESS.name());
        status.add(SpiderTaskStatus.TERMINATED.name());

        List<SpiderTaskEntity> spiderTaskEntities = spiderTaskDAO.selectByTaskTypeAndTaskTimeAndTaskJobAndStatusList(
                taskTime,
                SpiderTaskJob.M.name(),
                taskType,
                status
        );
        if(spiderTaskEntities.isEmpty()) {
            doInitNewTask(taskType, taskTime);
        }
    }

    private Date getTaskTime(SpiderTimeModel timeModel) {
        Date time = new Date();
        long currentTimeVal = (new Date()).getTime();
        long taskTimeVal = currentTimeVal/timeModel.getTimeValue() * timeModel.getTimeValue();
        return new Date(taskTimeVal);
    }



    private void doInitNewTask(String taskType, Date taskTime) {
        logger.info(String.format("insert new task for task type %s", taskType));
        String spiderId = UUID.randomUUID().toString();
        SpiderTaskEntity spiderTaskEntity = new SpiderTaskEntity();
        spiderTaskEntity.setSpiderId(spiderId);
        spiderTaskEntity.setTaskType(taskType);
        spiderTaskEntity.setTaskJob(SpiderTaskJob.E.name());
        spiderTaskEntity.setTaskTime(taskTime);
        spiderTaskDAO.insertOne(spiderTaskEntity);
        spiderTaskEntity.setTaskJob(SpiderTaskJob.M.name());
        spiderTaskDAO.insertOne(spiderTaskEntity);
    }

    private void handleRunMonitor(List<SpiderTaskEntity> monitorTaskList) {
        for(SpiderTaskEntity monitorTask : monitorTaskList) {
            if(SpiderTaskStatus.PROCESSING.name().equals(monitorTask.getTaskStatus())) {
                long lastActiveTime = monitorTask.getUpdatedAt().getTime();
                long currentTime = (new Date()).getTime();
                if((currentTime-lastActiveTime)>MAX_MONITOR_RUN_TIME) {
                    logger.info(String.format("restart monitor task.  monitor task:%s", monitorTask.toString()));
                    monitorTask.setTaskStatus(SpiderTaskStatus.NEW.name());
                    spiderTaskDAO.updateStatusWithoutVersion(monitorTask);
                }
            }
        }
    }

    protected void registTaskType(SpiderTaskType spiderTaskType,ITaskHandler taskHandler) {
        taskTypeMap.put(spiderTaskType.name(), taskHandler);
    }

    private void submitTask() {
        Set<String> taskTypeSet = taskTypeMap.keySet();
        for(String taskType : taskTypeSet) {
            execTask(taskType, SpiderTaskJob.M.name());
            execTask(taskType, SpiderTaskJob.E.name());
        }
    }


    private void execTask(String taskType, String taskJob) {
        ThreadPoolTaskExecutor executorPool = getThreadPool(taskType);
        List<SpiderTaskEntity> taskList = queryTask(taskType, taskJob);
        for(SpiderTaskEntity spiderTaskEntity: taskList) {
            executorPool.submit(new Runnable() {
                @Override
                public void run() {
                    doExecTask(spiderTaskEntity);
                }
            });
        }
    }

    private void doExecTask(SpiderTaskEntity spiderTaskEntity){
        ITaskHandler taskHandler = getTaskHandler(spiderTaskEntity.getTaskJob(), spiderTaskEntity.getTaskType());
        if(taskHandler == null) {
            logger.error(String.format("not found task handler for task type %s", spiderTaskEntity.getTaskType()));
            throw new RuntimeException("task handle is null");
        }
        if(lockTask(spiderTaskEntity)) {
            if(taskHandler.isStatisfied(spiderTaskEntity)) {
                boolean taskFinish = false;
                try {
                    taskFinish = taskHandler.doTask(spiderTaskEntity);
                }catch(Exception e) {
                    logger.error("exec task error",e);
                }
                if(taskFinish) {
                    finishTask(spiderTaskEntity);
                } else {
                    unlockTask(spiderTaskEntity);
                }
            } else {
                unlockTask(spiderTaskEntity);
            }
        }
    }

    private ITaskHandler getTaskHandler(String taskJob, String taskType) {
        if(SpiderTaskJob.E.name().equals(taskJob)) {
            return  taskTypeMap.get(taskType);
        }
        return monitorTaskHandler;
    }

    private ThreadPoolTaskExecutor getThreadPool(String taskJob) {
        if(SpiderTaskJob.E.name().equals(taskJob)) {
            return spiderExecutorPool;
        }
        return spiderMoniterPool;
    }

    private List<SpiderTaskEntity> queryTask(String taskType, String taskJob) {
        List<String> taskStatus = new ArrayList<>();
        taskStatus.add(SpiderTaskStatus.NEW.toString());
        return spiderTaskDAO.selectByTaskTypeAndTaskJobAndStatus(taskType, taskJob, taskStatus);
    }

    private void finishTask(SpiderTaskEntity spiderTaskEntity) {
        updateTaskStatus(spiderTaskEntity, SpiderTaskStatus.SUCCESS);
    }

    private boolean lockTask(SpiderTaskEntity spiderTaskEntity) {
        return updateTaskStatus(spiderTaskEntity, SpiderTaskStatus.PROCESSING);
    }

    private boolean unlockTask(SpiderTaskEntity spiderTaskEntity) {
        return updateTaskStatus(spiderTaskEntity, SpiderTaskStatus.NEW);
    }

    private boolean updateTaskStatus(SpiderTaskEntity spiderTaskEntity, SpiderTaskStatus nextStatus) {
        spiderTaskEntity.setTaskStatus(nextStatus.name());
        int taskType = spiderTaskDAO.updateStatus(spiderTaskEntity);
        if(taskType == 1) {
            spiderTaskEntity.setVersion(spiderTaskEntity.getVersion() + 1L);
            return true;
        }
        return false;
    }

}
