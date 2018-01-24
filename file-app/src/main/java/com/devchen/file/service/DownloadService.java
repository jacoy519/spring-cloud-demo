package com.devchen.file.service;

import com.devchen.file.common.Constant;
import com.devchen.file.dal.dao.DownloadTaskDao;
import com.devchen.file.dal.entity.DownloadTaskEntity;
import com.devchen.file.entity.DownloadResult;
import com.devchen.file.factory.DownloadTaskFactory;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service("downloadService")
public class DownloadService {

    private final static Logger logger = Logger.getLogger(DownloadService.class);

    @Resource
    private TransmissionDownloadService transmissionDownloadService;

    @Resource
    private WebgDownloadService webgDownloadService;

    @Resource
    private DownloadTaskDao downloadTaskDao;

    @Resource
    private ThreadPoolTaskExecutor downloadTaskHandler;

    private final AtomicInteger currentRunTaskNum = new AtomicInteger(0);

    public void acceptMagnetDownloadTask(String magentDownloadAddress, String localSaveDir, String fileName) {
        if(isSameTaskExist(fileName)) {
            return;
        }

        DownloadTaskEntity task = DownloadTaskFactory.createMagnetDownloadTask(magentDownloadAddress, localSaveDir, fileName);
        downloadTaskDao.insertDownloadTask(task);
        logger.info("accpet download task " + task.toString());
    }

    @Scheduled(fixedDelay = Constant.TEN_MINUTE)
    public void monitorDownloadProcess() {
        logger.info("try clean download process");
        transmissionDownloadService.monitorDownloadProcess();
    }

    @Scheduled(fixedDelay = Constant.FIVE_MINUTE)
    public synchronized void handleDownloadTask() {
        if(currentRunTaskNum.get() >= Constant.MAX_DOWNLOAD_TASK_NUM) {
            return;
        }
        int currentHandleTaskNumber = Constant.MAX_DOWNLOAD_TASK_NUM - currentRunTaskNum.get();
        List<DownloadTaskEntity> tasks = downloadTaskDao.selectTopAcceptArticle();
        for(int i=0 ;i< currentHandleTaskNumber && i< tasks.size(); i++) {
            final DownloadTaskEntity task = tasks.get(i);
            updateTaskStatusToRun(task);
            logger.info("run task " + task.toString());
            currentRunTaskNum.getAndAdd(1);
            downloadTaskHandler.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        switch (task.getDownloadType()) {
                            case "MD" :
                                handleMagentDownloadTask(task);
                                break;
                            default:
                                break;
                        }

                    } finally {
                        currentRunTaskNum.getAndAdd(-1);
                    }
                }
            });

        }
    }

    private void updateTaskStatusToRun(DownloadTaskEntity taskEntity) {
        downloadTaskDao.updateTaskDownloadStatusById(taskEntity.getId(), "RN");
    }

    private void updateTaskStatusToSuccess(DownloadTaskEntity taskEntity) {
        downloadTaskDao.updateTaskDownloadStatusById(taskEntity.getId(), "SC");
    }

    private void updateTaskStatusToFail(DownloadTaskEntity taskEntity) {
        downloadTaskDao.updateTaskDownloadStatusById(taskEntity.getId(), "FA");
    }

    private void handleMagentDownloadTask(DownloadTaskEntity taskEntity) {
        String magentDownloadAddress = taskEntity.getRemoteAddress();
        String localSaveDir = taskEntity.getLocalSaveDir();
        String fileName = taskEntity.getTaskId();
        DownloadResult result = null;
        try {
            if(!isDirExist(localSaveDir)) {
                FileUtils.forceMkdir(new File(localSaveDir));
            }
            result = transmissionDownloadService.submitDownloadTask(magentDownloadAddress, localSaveDir, fileName);

        } catch (Exception e) {
            logger.info("download magent fail ", e);
        } finally {
            if(result!=null && result.isSuccess()) {
                logger.info("magent download task success " + taskEntity.toString());
                updateTaskStatusToSuccess(taskEntity);
            } else{
                logger.info("magent download task fail " + taskEntity.toString());
                updateTaskStatusToFail(taskEntity);
            }
        };

    }

    private boolean isDirExist(String path) {
        File file = new File(path);
        return file.isDirectory();
    }


    private boolean isSameTaskExist(String taskId) {
        return downloadTaskDao.selectDownloadTaskByTaskId(taskId) != null;
    }



}
