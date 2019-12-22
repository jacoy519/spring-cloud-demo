package com.devchen.spider.service.biz.hexun;

import com.devchen.spider.enums.SpiderTaskType;
import com.devchen.spider.service.common.builder.SpiderContextBuilder;
import com.devchen.spider.service.common.entity.SpiderContext;
import com.devchen.spider.service.common.mgr.SpiderPageProcessorManager;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.annotation.Resource;

@Component
public class HexunSpiderContextBuilder implements SpiderContextBuilder {

    @Resource
    private SpiderPageProcessorManager spiderPageProcessorManager;

    @Override
    public SpiderTaskType getSpiderTaskType() {
        return SpiderTaskType.HEXUN;
    }

    @Override
    public SpiderContext buildSpiderContext() {

        SpiderContext spiderTaskConfig = new SpiderContext();

        return null;
    }


}
