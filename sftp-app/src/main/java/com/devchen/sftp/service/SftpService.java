package com.devchen.sftp.service;

import com.devchen.sftp.constant.ResultCode;
import com.devchen.sftp.controller.GlobalExceptionHandler;
import com.devchen.sftp.controller.request.SftpDownloadRequest;
import com.devchen.sftp.controller.resp.SftpDownloadResult;
import com.devchen.sftp.excpetion.FlowException;
import com.google.gson.Gson;
import com.jcraft.jsch.*;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Executable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

@Service
public class SftpService {

    @Value("${sftp.local.path}")
    private String sftpLocalPath;

    @Resource
    private ThreadPoolTaskExecutor downloadTaskHandler;




    private final static Logger logger = Logger.getLogger(GlobalExceptionHandler.class);


    public void submitSftpDownload(SftpDownloadRequest request) {
        if(isDownloadFinish(request)) {
            return;
        } else {
            try {
                downloadTaskHandler.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sftpDownload(request);
                        } catch (Exception e) {
                            logger.error(String.format("[run] run thread error for request %s",
                                    (new Gson()).toJson(request)));
                        }

                    }
                });
            } catch (Exception e) {
                logger.error(String.format("[submitSftpDownload] submit task error for request %s", (new Gson()).toJson(request)), e);
            }
            throw new FlowException(ResultCode.PROCESSING);
        }

    }


    private boolean isDownloadFinish(SftpDownloadRequest request) {
        String currentPath = sftpLocalPath + "/" + request.getRequestId();
        File dirFile = new File(currentPath);
        String downloadEndTagFile = currentPath + "/" + request.getFileName() + ".end";
        File endFile = new File(downloadEndTagFile);
        if(dirFile.exists() && endFile.exists()) {
            logger.info(String.format("[checkRequestExist] dir path and end file exist %s",currentPath));
            return true;
        }

        if(!dirFile.exists()) {
            logger.info(String.format("[checkRequestExist] dir path and end file exist %s",currentPath));
            return false;
        }

        if(dirFile.exists() && !endFile.exists()) {
            try {
                Path dirPath = Paths.get(currentPath);
                BasicFileAttributes att = Files.readAttributes(dirPath, BasicFileAttributes.class);
                long dirCreateSec =  att.creationTime().toMillis() / 1000L;
                long currentSec = (new Date()).getTime()/1000;
                if((currentSec - dirCreateSec) > request.getProxySftpMaxDownloadSec()) {
                    logger.info(String.format("[isDownloadFinish] proxy sftp download out of time and need to retry. dir path %s",currentPath));
                    FileUtils.forceDelete(dirFile);
                    return false;
                } else {
                    logger.info(String.format("[isDownloadFinish] proxy sftp download in process. dir path %s",currentPath));
                }
            } catch (Exception e) {
                logger.error("[isDownloadFinish] handle out of time error need retry");
                throw new FlowException(ResultCode.PROCESSING);
            }

        }
        throw new FlowException(ResultCode.PROCESSING);
    }


    public void sftpDownload(SftpDownloadRequest request) {
        JSch jsch = new JSch();
        Channel channel = null;
        ChannelSftp sftp = null;
        Session sshSession = null;

        String currentPath = sftpLocalPath + "/" + request.getRequestId();
        File dirFile = new File(currentPath);
        Random random = new Random();
        while(true) {
            if(dirFile.exists()) {
                logger.info(String.format("[sftpDownload] file is exist and cancel to run %s", currentPath));
                return;
            }
            try {
                FileUtils.forceMkdir(dirFile);
                logger.info(String.format("[sftpDownload] success create dir %s", currentPath));
                break;
            } catch (Exception e) {
                logger.error(e);
                try {
                    Thread.sleep(1000 + (random.nextInt() %1000));
                } catch (Exception ex) {
                    logger.error(ex);
                }

            }
        }

        try {
            sshSession = jsch.getSession(request.getUseName(), request.getHost(), Integer.valueOf(request.getPort()));
            sshSession.setPassword(request.getPassword());
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            sftp.cd(request.getRemotePath());
            String sftpPath = request.getRemotePath() + "/" + request.getFileName();
            String localPath = currentPath + "/" + request.getFileName();
            logger.info(String.format("[sftpDownload] start to download file from remote sftp path %s to local path %s",
                    sftpPath, localPath));

            SftpProgressMonitor sftpProgressMonitor = new SftpProgressMonitor() {

                private long total = 0L;

                private String src;

                private String dest;

                @Override
                public void init(int op, String src, String dest, long max) {
                    logger.info(String.format("[sftpProgressMonitor] start tp download from %s to %s",
                            src, dest
                            ));
                    this.src = src;
                    this.dest = dest;
                }

                @Override
                public boolean count(long count) {
                    total = total + count;
                    if(total % (1024 * 1024) == 0 ) {
                        logger.info(String.format("[sftpProgressMonitor] continue to download from %s to %s. download total size %s MB",
                                src, dest, String.valueOf(total / (1024 * 1024))
                        ));
                    }
                    return true;
                }

                @Override
                public void end() {
                    logger.info(String.format("[sftpProgressMonitor] finish to download from %s to %s. download total size %s byte",
                            src, dest, String.valueOf(total)
                    ));
                }
            };
            sftp.get(sftpPath, localPath, sftpProgressMonitor);
            logger.info(String.format("[sftpDownload] end to download file from remote sftp path %s to local path %s and start to create end file",
                    sftpPath, localPath));
            String endFilePath = localPath + ".end";
            File endFile = new File(endFilePath);
            endFile.createNewFile();
            logger.info(String.format("[sftpDownload] success to download file from remote sftp path %s to local path %s",
                    sftpPath, localPath));
        } catch (Exception e) {
            logger.error(e);
            if(dirFile.exists()) {
                try {
                    FileUtils.forceDelete(dirFile);
                } catch (Exception ex) {
                    logger.error("clear file", ex);
                }

            }
        } finally {
            if (sftp != null) {
                if (sftp.isConnected()) {
                    sftp.disconnect();
                }
            }
            if (sshSession != null) {
                if (sshSession.isConnected()) {
                    sshSession.disconnect();
                }
            }
        }
    }
}
