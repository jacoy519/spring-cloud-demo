package com.devchen.spider.job;

import com.devchen.spider.service.biz.getupload.GetUploadSpiderTaskService;
import com.devchen.spider.service.biz.zimuzu.ZimuzuSpiderTaskService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SpiderJob {

    @Resource
    private ZimuzuSpiderTaskService zimuzuSpiderTaskService;

    @Resource
    private GetUploadSpiderTaskService getUploadSpiderTaskService;


    @Scheduled(fixedDelay = 120L * 1000L)
    public  void runSpider() {
        zimuzuSpiderTaskService.run();
        getUploadSpiderTaskService.run();
    }

}
