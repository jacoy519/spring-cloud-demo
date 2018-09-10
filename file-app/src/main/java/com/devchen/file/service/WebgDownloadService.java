package com.devchen.file.service;

import com.devchen.file.entity.DownloadResult;
import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


@Service("webgDownloadService")
public class WebgDownloadService extends ProcessDownloadService {

    private final static Logger logger = Logger.getLogger(WebgDownloadService.class);


    @Resource
    private ThreadPoolTaskExecutor commonMsgHandler;

    @Resource
    private ThreadPoolTaskExecutor errorMsgHandler;

    public DownloadResult submitDownloadTask(String torrentSaveAddress, String downloadAddress,Long taskId) {
        String cmd = createWebgCmd(torrentSaveAddress, downloadAddress);
        return runDownloadCmd(cmd, commonMsgHandler, errorMsgHandler, taskId);
    }

    private String createWebgCmd(String torrentSaveAddress, String downloadAddress) {
        String downloadCmd = "wget --tries=40 --no-check-certificate -O %s %s ";
        return String.format(downloadCmd,torrentSaveAddress,downloadAddress);
    }

    @Override
    protected DownloadResult handleCommonMsg(InputStream is, Long taskId) {
        Reader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = null;
        try {
            while((line = bufferedReader.readLine()) != null) {
                logger.info("common:"  + line);
            }
        } catch (Exception e) {
            logger.error("io thread error",e);
        }
        return createSuccessDownloadResult();
    }

    @Override
    protected DownloadResult handleErrorMsg(InputStream is,  Long taskId) {
        Reader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = null;
        try {
            while((line = bufferedReader.readLine()) != null) {
                logger.info("error:" +line);
            }
        } catch (Exception e) {
            logger.error("io thread error",e);
        }
        return createSuccessDownloadResult();
    }
}
