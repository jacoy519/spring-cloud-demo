package com.devchen.spider.service.biz.weixin.lcFunds.handler;

import com.devchen.spider.dal.entity.SpiderTaskEntity;
import com.devchen.spider.service.common.handler.ITaskHandler;
import com.devchen.spider.service.common.spider.SpiderRedisScheduler;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import javax.annotation.Resource;

@Service
public class LcFundsTaskHandler  implements ITaskHandler {

    @Resource
    private LcFundsPageProcesser lcFundsPageProcesser;

    @Resource
    private SpiderRedisScheduler spiderRedisScheduler;

    @Resource
    private LcFundDownloader lcFundDownloader;

    @Override
    public boolean isStatisfied(SpiderTaskEntity spiderTaskEntity) {
        return true;
    }

    @Override
    public boolean doTask(SpiderTaskEntity spiderTaskEntity) {
        Spider spider = Spider.create(lcFundsPageProcesser);
        spider.setUUID(spiderTaskEntity.getSpiderId())
                .setScheduler(spiderRedisScheduler)
                .setDownloader(lcFundDownloader)
                .addUrl("http://mp.weixin.qq.com/profile?src=3&timestamp=1552096504&ver=1&signature=ogho7oxxB5aay*eNC7aDEf2t5RrDPOKhAOTmZ65qXtDcnDaaZutiBH9zXxD36Qgv3SeG5LzgbO4Pm5z0PThZVQ==").thread(3).run();

        return true;
    }
}
