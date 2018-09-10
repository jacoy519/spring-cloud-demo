package com.devchen.file.service;

import com.devchen.file.common.Constant;
import com.devchen.file.entity.DownloadResult;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("transmissionDownloadService")
public class TransmissionDownloadService extends ProcessDownloadService {

    private final static Logger logger = Logger.getLogger(TransmissionDownloadService.class);

    @Resource
    private ThreadPoolTaskExecutor commonMsgHandler;

    @Resource
    private ThreadPoolTaskExecutor errorMsgHandler;

    @PostConstruct
    public void init() {
        File file = new File(Constant.DOWNLOAD_LOG_SAVE_DIR);
        if(!file.exists()) {
            try {
                FileUtils.forceMkdir(file);
            } catch (Exception e) {
                logger.error(String.format("create download save dir error %s", Constant.DOWNLOAD_LOG_SAVE_DIR));
            }

        }
    }

    @Override
    protected DownloadResult handleCommonMsg(InputStream is, Long taskId) {
        Reader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = null;
        Pattern speedPattern = Pattern.compile("([0-9]*.[0-9]%), dl");
        String speed = "";
        File logFile = getDownloadLogFile(taskId);
        try {
            while((line = bufferedReader.readLine()) != null) {
                FileUtils.writeStringToFile(logFile, "common msg:" + line + "\n", true);
                Matcher speedMatcher = speedPattern.matcher(line);
                if(speedMatcher.find()) {
                    String currentSpeed = speedMatcher.group(1);
                    if(!speed.equals(currentSpeed)) {
                        speed = currentSpeed;
                        logger.info(taskId + " : " + speed);
                    }

                }
            }
            if("50".compareTo(speed) > 0) {
                return createSuccessDownloadResult();
            }
        } catch (Exception e) {
            logger.error(taskId+" io thread error",e);
            return createFailDownloadResult(e.getMessage());
        }
        return createSuccessDownloadResult();
    }

    @Override
    protected DownloadResult handleErrorMsg(InputStream is, Long taskId) {
        Reader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = null;
        File logFile = getDownloadLogFile(taskId);
        try {
            while((line = bufferedReader.readLine()) != null) {
                FileUtils.writeStringToFile(logFile, "error msg:" + line + "\n", true);
            }
        } catch (Exception e) {
            logger.error(taskId+" io thread error",e);
            return createFailDownloadResult(e.getMessage());
        }
        return createSuccessDownloadResult();
    }

    private synchronized File getDownloadLogFile(Long taskId) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String downloadFileName = taskId + "_" + df.format(date);
        String filePath = Constant.DOWNLOAD_LOG_SAVE_DIR + "/" + downloadFileName + ".log";
        File file = new File(filePath);
        if(!file.exists()) {
            try {
                file.createNewFile();
            }catch (Exception e) {
                logger.error(String.format("fail to create download log file %s", filePath));
            }
        }
        return file;
    }

    private String getTransmissionDownloadCmd(String mergeAddress, String downLoadDir) {
        long randLong = (new Random()).nextLong();
        long ipLong = 50000L + (randLong%10000);
        downLoadDir = downLoadDir.replaceAll(" ","");
        return String.format(Constant.TRANSMISSION_MERGE_DOWNLOAD_CMD_FORMAT, mergeAddress, downLoadDir, String.valueOf(ipLong));
    }

    public DownloadResult submitDownloadTask(String mergeAddress, String downLoadDir, Long taskId) {
        String downLoadCmd = getTransmissionDownloadCmd(mergeAddress, downLoadDir);
        return runDownloadCmd(downLoadCmd, commonMsgHandler, errorMsgHandler, taskId);
    }

    public void monitorDownloadProcess() {
        closeOutTimeDownloadProcess();
    }

    public static void main(String[] args) {
        long randLong = (new Random()).nextLong();
        long ipLong = 50000L + (randLong%10000);
        System.out.print(ipLong);
    }
}
