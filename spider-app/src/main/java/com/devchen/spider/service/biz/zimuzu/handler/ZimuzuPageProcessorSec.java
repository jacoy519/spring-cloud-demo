package com.devchen.spider.service.biz.zimuzu.handler;

import com.devchen.spider.enums.SpiderTaskType;
import com.devchen.spider.service.common.pageProcessor.AbstractPageProcessor;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

@Component
public class ZimuzuPageProcessorSec extends AbstractPageProcessor {

    @Override
    public SpiderTaskType getSpiderTaskType() {
        return SpiderTaskType.ZIMUZU_FAV_SPIDER;
    }

    @Override
    public void process(Page page) {

    }

    @Override
    public Site getSite() {
        return null;
    }
}
