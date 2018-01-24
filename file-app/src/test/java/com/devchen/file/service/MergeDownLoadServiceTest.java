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
    public void testDownload() {

//        videoDownloadScheduler.findVideoDownload();

        downloadService.handleDownloadTask();

        try {
            Thread.sleep(30000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    //@Test
    public void testDownload2() {
        String saveDir = "/root/downloads/test.torrent";

        String address = "magnet:?xt=urn:btih:bf39d57485418cffcaf0ca82e5ff6bb1e4decad6&tr=udp://9.rarbg.to:2710/announce&tr=udp://9.rarbg.me:2710/announce&tr=http://tr.cili001.com:8070/announce&tr=http://tracker.trackerfix.com:80/announce&tr=udp://open.demonii.com:1337&tr=udp://tracker.opentrackr.org:1337/announce&tr=udp://p4p.arenabg.com:1337&tr=wss://tracker.openwebtorrent.com&tr=wss://tracker.btorrent.xyz&tr=wss://tracker.fastcast.nz";
        String torrentAddress = "https://ehtracker.org/get/540802/25be68608d6d51c9ee56ab839d44771ee6315cae.torrent";
        webgDownloadService.submitDownloadTask(saveDir, torrentAddress);

    }
}
