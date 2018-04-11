package com.devchen.file.service;


import com.devchen.file.scheduler.VideoDownloadScheduler;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MergeDownLoadServiceTest {

    private final static Logger logger = Logger.getLogger(MergeDownLoadServiceTest.class);

    @Resource
    private TransmissionDownloadService transmissionDownloadService;

    @Resource
    private WebgDownloadService webgDownloadService;

    @Resource
    private DownloadService downloadService;

    @Resource
    private VideoDownloadScheduler videoDownloadScheduler;

    @Test
    public void testDownload3() {
        videoDownloadScheduler.findVideoDownload();
    }


    //@Test
    public void testDownload2() {
        String saveDir = "/root/downloads/test.torrent";

        String address = "/save/Dir";
        String torrentAddress = "https://ehtracker.org/get/1105465/309f58f793120fe1dec01f4665e232d488b44f71.torrent";
        webgDownloadService.submitDownloadTask(saveDir, torrentAddress);

        transmissionDownloadService.submitDownloadTask(saveDir, address, saveDir);

    }
}
