package com.devchen.file.service;

import com.devchen.file.common.Constant;
import com.devchen.file.entity.DownloadResult;
import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public abstract class ProcessDownloadService {


    private final static Logger logger = Logger.getLogger(ProcessDownloadService.class);

    private Set<ProcessContext> processContextSet = ConcurrentHashMap.<ProcessContext> newKeySet();


    protected DownloadResult runDownloadCmd(String cmd, ThreadPoolTaskExecutor commonMsgMonitor, ThreadPoolTaskExecutor errorMsgMonitor, Long taskId) {
        logger.info(String.format("create download process. taskId:%s, cmd:%s", taskId, cmd));
        Process process = null;
        ProcessContext processContext = null;
        DownloadResult downloadResult = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            processContext = createProcessContext(process, taskId);
            processContextSet.add(processContext);
            Future<DownloadResult> commonFuture = commonMsgMonitor.submit(new CommonMsgMonitorCallable(process, taskId));
            Future<DownloadResult> errorFuture = errorMsgMonitor.submit(new ErrorMsgMonitorCallable(process, taskId));

            DownloadResult commonResult = commonFuture.get();
            DownloadResult errorResult = errorFuture.get();
            downloadResult = createFinalDownloadResult(commonResult, errorResult);
        }catch (Exception e) {
            logger.error("download file fail when exec " + cmd, e);
            downloadResult = createFailDownloadResult(e.getMessage());
        } finally {
            if(process !=null && process.isAlive()) {
                process.destroyForcibly();
            }
            processContextSet.remove(processContext);
        }
        return downloadResult;
    }


    protected void closeOutTimeDownloadProcess() {
        Date currentDate = new Date();
        Set<ProcessContext> needCloseProcessSet = new HashSet<>();
        for(ProcessContext processContext: processContextSet) {
            if(processContext.getOutTime() < currentDate.getTime()) {
                needCloseProcessSet.add(processContext);
            }
        }
        for(ProcessContext needCloseProcessContext : needCloseProcessSet) {
            logger.info("the download process " + needCloseProcessContext.taskId + " is out of time and force to be closed");
            needCloseProcessContext.getProcess().destroyForcibly();
            processContextSet.remove(needCloseProcessContext);
        }
    }


    private ProcessContext createProcessContext(Process process, Long taskId) {
        Date currentDate = new Date();
        return new ProcessContext(currentDate.getTime() + Constant.DOWNLOAD_TASK_MAX_RUN_TIME, process, taskId);
    }



    private DownloadResult createFinalDownloadResult(DownloadResult commonResult, DownloadResult errorResult) {
        if(commonResult == null) {
            return createFailDownloadResult("the common result is null" );
        }
        if(errorResult == null) {
            return createFailDownloadResult(" the error result is null");
        }
        if(!commonResult.isSuccess()) {
            return createFailDownloadResult("the common result is fail " + commonResult.getErrorMsg());
        }
        if(!errorResult.isSuccess()) {
            return createFailDownloadResult("the error result is fail " + errorResult.getErrorMsg());
        }
        return createSuccessDownloadResult();
    }

    protected DownloadResult createFailDownloadResult(String errorMsg) {
        return new DownloadResult(false, errorMsg);
    }

    protected DownloadResult createSuccessDownloadResult() {
        return new DownloadResult(true,null);
    }

    protected abstract DownloadResult handleCommonMsg(InputStream inputStream, Long taskId);

    protected abstract DownloadResult handleErrorMsg(InputStream inputStream, Long taskId);

    private class ProcessContext {

        private Process process;

        private Long outTime;

        private Long taskId;

        ProcessContext(Long outTime,Process process, Long taskId) {
            this.outTime = outTime;
            this.process = process;
            this.taskId = taskId;
        }

        public Long getOutTime() {
            return outTime;
        }

        public Process getProcess() {
            return process;
        }
    }

    private class CommonMsgMonitorCallable implements Callable<DownloadResult> {

        private Process process;

        private Long taskId;

        public CommonMsgMonitorCallable(Process process, Long taskId) {
            this.process = process;
            this.taskId = taskId;
        }

        @Override
        public DownloadResult call() throws Exception {
            return handleCommonMsg(process.getInputStream(), taskId);
        }
    }

    private class ErrorMsgMonitorCallable implements Callable<DownloadResult> {

        private Process process;

        private Long taskId;

        public ErrorMsgMonitorCallable(Process process, Long taskId) {
            this.process = process;
            this.taskId = taskId;
        }

        @Override
        public DownloadResult call() throws Exception {
            return handleErrorMsg(process.getErrorStream(), taskId);
        }
    }
}
