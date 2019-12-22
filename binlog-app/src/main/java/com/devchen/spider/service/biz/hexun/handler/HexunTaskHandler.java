package com.devchen.spider.service.biz.hexun.handler;

import com.devchen.spider.dal.entity.SpiderTaskEntity;
import com.devchen.spider.service.common.handler.ITaskHandler;
import com.devchen.spider.service.common.spider.SpiderRedisScheduler;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import javax.annotation.Resource;

@Service
public class HexunTaskHandler  implements ITaskHandler {

    @Resource
    private HexunPageProcesser hexunPageProcesser;

    @Resource
    private HexunPipeline hexunPipeline;

    @Resource
    private SpiderRedisScheduler spiderRedisScheduler;

    @Override
    public boolean isStatisfied(SpiderTaskEntity spiderTaskEntity) {
        return true;
    }

    @Override
    public boolean doTask(SpiderTaskEntity spiderTaskEntity) {
        Spider spider = Spider.create(hexunPageProcesser);
        spider.setUUID(spiderTaskEntity.getSpiderId())
                .addPipeline(hexunPipeline)
                .setScheduler(spiderRedisScheduler)
                .addUrl("http://stock.hexun.com/2019-02-25/196289333.html").thread(3).run();

        return true;
    }
}
