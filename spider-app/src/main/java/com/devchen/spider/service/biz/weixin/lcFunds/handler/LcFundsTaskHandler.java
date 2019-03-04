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

    @Override
    public boolean isStatisfied(SpiderTaskEntity spiderTaskEntity) {
        return true;
    }

    @Override
    public boolean doTask(SpiderTaskEntity spiderTaskEntity) {
        Spider spider = Spider.create(lcFundsPageProcesser);
        spider.setUUID(spiderTaskEntity.getSpiderId())
                .setScheduler(spiderRedisScheduler)
                .addUrl("http://weixin.sogou.com/weixin?type=1&s_from=input&query=lc_funds&ie=utf8&_sug_=n&_sug_type_=").thread(3).run();

        return true;
    }
}
