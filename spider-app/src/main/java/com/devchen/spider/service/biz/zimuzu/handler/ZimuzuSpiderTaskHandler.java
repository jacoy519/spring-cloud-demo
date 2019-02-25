package com.devchen.spider.service.biz.zimuzu.handler;

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

@Service
public class ZimuzuSpiderTaskHandler implements ITaskHandler {

    private final static Logger logger = Logger.getLogger(ZimuzuSpiderTaskHandler.class);

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

    @Override
    public boolean isStatisfied(SpiderTaskEntity spiderTaskEntity) {
        return true;
    }

    @Override
    public boolean doTask(SpiderTaskEntity spiderTaskEntity) {
        Spider spider = Spider.create(zimuzuPageProcessor);
        spider.setUUID(spiderTaskEntity.getSpiderId())
                .addPipeline(zimuzuPipeline)
                .setScheduler(spiderRedisScheduler)
                .addUrl(appProperty.getZimuzuFavUrl()).thread(3).run();
        return true;
    }
}
