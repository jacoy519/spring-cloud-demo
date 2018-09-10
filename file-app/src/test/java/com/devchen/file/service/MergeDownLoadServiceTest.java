package com.devchen.file.service;


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


}
