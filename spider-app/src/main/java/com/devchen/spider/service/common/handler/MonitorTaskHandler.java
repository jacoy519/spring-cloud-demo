package com.devchen.spider.service.common.handler;

import com.devchen.spider.dal.dao.SpiderTaskDAO;
import com.devchen.spider.dal.entity.SpiderTaskEntity;
import com.devchen.spider.enums.SpiderTaskJob;
import com.devchen.spider.enums.SpiderTaskStatus;
<<<<<<< HEAD
import com.devchen.spider.service.common.spider.SpiderRedisScheduler;
=======
>>>>>>> 5d9c7d42bd0f07d5186e7ee32dff8bb0572fd9e5
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MonitorTaskHandler implements ITaskHandler {

    @Resource
    private SpiderTaskDAO spiderTaskDAO;

<<<<<<< HEAD
    @Resource
    private SpiderRedisScheduler spiderRedisScheduler;

    private final static long maxTime = 3600000L;
=======
    private final static long maxTime = 1800000;
>>>>>>> 5d9c7d42bd0f07d5186e7ee32dff8bb0572fd9e5

    private final static Logger logger = Logger.getLogger(MonitorTaskHandler.class);

    @Override
    public boolean isStatisfied(SpiderTaskEntity spiderTaskEntity) {
        return true;
    }

    @Override
    public boolean doTask(SpiderTaskEntity spiderTaskEntity) {
        logger.info(String.format("exec monitor task %s", spiderTaskEntity.toString()));
        List<SpiderTaskEntity> executorSpiderTaskList = getExecutorTask(spiderTaskEntity);
        if(!executorSpiderTaskList.isEmpty()) {
            return handleRunExecutorTask(executorSpiderTaskList,spiderTaskEntity);
        } else {
            return handleNotRunExecutorTask(spiderTaskEntity);
        }
    }


    private boolean handleRunExecutorTask(List<SpiderTaskEntity> spiderTaskEntities, SpiderTaskEntity monitorTask) {
        for(SpiderTaskEntity spiderTaskEntity :spiderTaskEntities) {
            if(SpiderTaskStatus.PROCESSING.name().equals(spiderTaskEntity.getTaskStatus())) {
                long lastActiveTime = spiderTaskEntity.getUpdatedAt().getTime();
                long currentTime = (new Date()).getTime();
                if((currentTime-lastActiveTime)>maxTime) {
                    logger.info(String.format("end task. executor task:%s, monitor task:%s",spiderTaskEntities.toString(), monitorTask.toString()));
                    spiderTaskEntity.setTaskStatus(SpiderTaskStatus.TERMINATED.name());
                    spiderTaskDAO.updateStatusWithoutVersion(spiderTaskEntity);
                }
            }
        }
        return false;
    }

    private boolean handleNotRunExecutorTask(SpiderTaskEntity spiderTaskEntity) {
        logger.info(String.format("end task. monitor task:%s", spiderTaskEntity.toString()));
<<<<<<< HEAD
        if(spiderRedisScheduler.getLeftRequestCount(spiderTaskEntity.getSpiderId()) > 0) {
            SpiderTaskEntity newSpiderTaskEntity = new SpiderTaskEntity();
            newSpiderTaskEntity.setSpiderId(spiderTaskEntity.getSpiderId());
            newSpiderTaskEntity.setTaskType(spiderTaskEntity.getTaskType());
            newSpiderTaskEntity.setTaskJob(SpiderTaskJob.E.name());
            newSpiderTaskEntity.setTaskTime(spiderTaskEntity.getTaskTime());
            newSpiderTaskEntity.setTaskStatus(SpiderTaskStatus.NEW.name());
            spiderTaskDAO.insertOne(spiderTaskEntity);
            return false;
        } else {
            spiderRedisScheduler.cleanRedis(spiderTaskEntity.getSpiderId());
            return true;
        }
=======
        return true;
>>>>>>> 5d9c7d42bd0f07d5186e7ee32dff8bb0572fd9e5
    }

    private List<SpiderTaskEntity> getExecutorTask(SpiderTaskEntity spiderTaskEntity) {
        List<String> status = new ArrayList<>();
        status.add(SpiderTaskStatus.PROCESSING.name());
        status.add(SpiderTaskStatus.NEW.name());
        List<SpiderTaskEntity> executorSpiderTask = spiderTaskDAO.selectBySpiderIdAndTaskStatusAndTaskJob(SpiderTaskJob.E.name(),
                spiderTaskEntity.getSpiderId(),
                status);
        return executorSpiderTask;
    }
}
