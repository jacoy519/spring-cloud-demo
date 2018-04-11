package com.devchen.file.factory;

import com.devchen.file.dal.entity.DownloadTaskEntity;

public class DownloadTaskFactory {

    public static DownloadTaskEntity createMagnetDownloadTask(String magentDownloadAddress, String localSaveDir, String fileName) {
        DownloadTaskEntity downloadTask = new DownloadTaskEntity();
        downloadTask.setRemoteAddress(magentDownloadAddress);
        downloadTask.setDownloadType("MD");
        downloadTask.setLocalSaveDir(localSaveDir);
        downloadTask.setTaskId(fileName);
        downloadTask.setDownloadStatus("AC");
        return downloadTask;
    }

    public static DownloadTaskEntity createRemoteTorrentDownloadTask(String remoteTorrentAddress, String localSaveDir) {
        DownloadTaskEntity downloadTask = new DownloadTaskEntity();
        downloadTask.setRemoteAddress(remoteTorrentAddress);
        downloadTask.setDownloadType("RT");
        downloadTask.setLocalSaveDir(localSaveDir);
        downloadTask.setTaskId(remoteTorrentAddress);
        downloadTask.setDownloadStatus("AC");
        return downloadTask;
    }
}
