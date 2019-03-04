package com.devchen.spider.service.biz.zimuzu.handler;

<<<<<<< HEAD
import com.devchen.spider.common.AppProperty;
import com.devchen.spider.dal.entity.SpiderTaskEntity;
import com.devchen.spider.service.biz.getupload.handler.GetUploadSpiderListener;
import com.devchen.spider.service.common.handler.ITaskHandler;
import com.devchen.spider.service.common.spider.SpiderRedisScheduler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
=======
import com.devchen.spider.dal.entity.SpiderTaskEntity;
import com.devchen.spider.service.common.handler.ITaskHandler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
>>>>>>> 5d9c7d42bd0f07d5186e7ee32dff8bb0572fd9e5

@Service
public class ZimuzuSpiderTaskHandler implements ITaskHandler {

    private final static Logger logger = Logger.getLogger(ZimuzuSpiderTaskHandler.class);

<<<<<<< HEAD
    @Resource
    private ZimuzuDownloader zimuzuDownloader;

    @Resource
    private ZimuzuPageProcessor zimuzuPageProcessor;

    @Resource
    private ZimuzuPipeline zimuzuPipeline;

    @Resource
    private SpiderRedisScheduler spiderRedisScheduler;


    @Resource
    private AppProperty appProperty;

=======
>>>>>>> 5d9c7d42bd0f07d5186e7ee32dff8bb0572fd9e5
    @Override
    public boolean isStatisfied(SpiderTaskEntity spiderTaskEntity) {
        return true;
    }

    @Override
    public boolean doTask(SpiderTaskEntity spiderTaskEntity) {
<<<<<<< HEAD
        Spider spider = Spider.create(zimuzuPageProcessor);
        spider.setUUID(spiderTaskEntity.getSpiderId())
                .addPipeline(zimuzuPipeline)
                .setScheduler(spiderRedisScheduler)
                .addUrl(appProperty.getZimuzuFavUrl()).thread(3).run();
=======
        logger.info(String.format("run spider task %s", spiderTaskEntity.toString()));
        try {
            Thread.sleep(40000);
        }catch (Exception e) {

        }
        logger.info(String.format("finish spider task %s", spiderTaskEntity.toString()));
>>>>>>> 5d9c7d42bd0f07d5186e7ee32dff8bb0572fd9e5
        return true;
    }
}
