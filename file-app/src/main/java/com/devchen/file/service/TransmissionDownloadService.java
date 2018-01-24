package com.devchen.file.service;

import com.devchen.file.common.Constant;
import com.devchen.file.entity.DownloadResult;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("transmissionDownloadService")
public class TransmissionDownloadService extends ProcessDownloadService {

    private final static Logger logger = Logger.getLogger(TransmissionDownloadService.class);

    @Resource
    private ThreadPoolTaskExecutor commonMsgHandler;

    @Resource
    private ThreadPoolTaskExecutor errorMsgHandler;

    @Override
    protected DownloadResult handleCommonMsg(InputStream is, String processName) {
        Reader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = null;
        Pattern speedPattern = Pattern.compile("([0-9]*.[0-9]%), dl");
        String speed = "";
        try {
            while((line = bufferedReader.readLine()) != null) {

                Matcher speedMatcher = speedPattern.matcher(line);
                if(speedMatcher.find()) {
                    String currentSpeed = speedMatcher.group(1);
                    if(!speed.equals(currentSpeed)) {
                        speed = currentSpeed;
                        logger.info(processName + " : " + speed);
                    }

                }
            }
        } catch (Exception e) {
            logger.error(processName+" io thread error",e);
            return createFailDownloadResult(e.getMessage());
        }
        return createSuccessDownloadResult();
    }

    @Override
    protected DownloadResult handleErrorMsg(InputStream is, String processName) {
        Reader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = null;
        try {
            while((line = bufferedReader.readLine()) != null) {

            }
        } catch (Exception e) {
            logger.error(processName+" io thread error",e);
            return createFailDownloadResult(e.getMessage());
        }
        return createSuccessDownloadResult();
    }

    private String getTransmissionDownloadCmd(String mergeAddress, String downLoadDir) {
        long randLong = (new Random()).nextLong();
        long ipLong = 50000L + (randLong%10000);
        return String.format(Constant.TRANSMISSION_MERGE_DOWNLOAD_CMD_FORMAT, mergeAddress, downLoadDir, String.valueOf(ipLong));
    }

    public DownloadResult submitDownloadTask(String mergeAddress, String downLoadDir, String fileName) {
        String downLoadCmd = getTransmissionDownloadCmd(mergeAddress, downLoadDir);
        return runDownloadCmd(downLoadCmd, commonMsgHandler, errorMsgHandler, fileName);
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
