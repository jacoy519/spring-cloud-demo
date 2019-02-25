package com.devchen.spider.service.biz.getupload.handler;

import com.devchen.spider.component.download.SocksHttpDownloader;
import com.devchen.spider.dal.dao.SpiderTaskDAO;
import com.devchen.spider.dal.entity.SpiderTaskEntity;
import com.devchen.spider.service.common.handler.ITaskHandler;
import com.devchen.spider.service.common.spider.SpiderRedisScheduler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class GetUploadSpiderTaskHandler implements ITaskHandler {

    private final static Logger logger = Logger.getLogger(GetUploadSpiderTaskHandler.class);

    @Resource
    private GetUploadPageProcesser getUploadPageProcesser;

    @Resource
    private GetUploadPipeline getUploadPipeline;

    @Resource
    private SocksHttpDownloader socksHttpDownloader;

    @Resource
    private SpiderTaskDAO spiderTaskDAO;

    @Resource
    private SpiderRedisScheduler spiderRedisScheduler;

    @Override
    public boolean isStatisfied(SpiderTaskEntity spiderTaskEntity) {
        return true;
    }

    @Override
    public boolean doTask(SpiderTaskEntity spiderTaskEntity) {
        Spider spider = Spider.create(getUploadPageProcesser);
        GetUploadSpiderListener getUploadSpiderListener = new GetUploadSpiderListener(spiderTaskDAO, spider, spiderTaskEntity);
        List<SpiderListener> spiderListenerList = new ArrayList<>();
        spiderListenerList.add(getUploadSpiderListener);
        spider.setUUID(spiderTaskEntity.getSpiderId())
                .setSpiderListeners(spiderListenerList)
                .addPipeline(getUploadPipeline)
                .setScheduler(spiderRedisScheduler)
                .setDownloader(socksHttpDownloader)
                .addUrl("https://ux.getuploader.com/cm3d2/index/date/desc/1")
                .addUrl("https://ux.getuploader.com/cm3d2_b/index/date/desc/1")
                .addUrl("https://ux.getuploader.com/cm3d2_c/index/date/desc/1")
                .addUrl("https://ux.getuploader.com/cm3d2_d/index/date/desc/1")
                .addUrl("https://ux.getuploader.com/cm3d2_e/index/date/desc/1")
                .addUrl("https://ux.getuploader.com/cm3d2_f/index/date/desc/1")
                .addUrl("https://ux.getuploader.com/cm3d2_g/index/date/desc/1")
                .addUrl("https://ux.getuploader.com/cm3d2_h/index/date/desc/1")
                .addUrl("https://ux.getuploader.com/cm3d2_i/index/date/desc/1")
                .addUrl("https://ux.getuploader.com/cm3d2_j/index/date/desc/1")
                .addUrl("https://ux.getuploader.com/cm3d2_k/index/date/desc/1")
                .addUrl("https://ux.getuploader.com/cm3d2_l/index/date/desc/1")
                .addUrl("https://ux.getuploader.com/melala001/index/date/desc/1")
                .addUrl("https://ux.getuploader.com/test_20160728/index/date/desc/1")
                .addUrl("https://ux.getuploader.com/tokinagare/index/date/desc/1")
                .addUrl("https://ux.getuploader.com/reaper7092/index/date/desc/1")
                .thread(1).run();

        return true;
    }
}
