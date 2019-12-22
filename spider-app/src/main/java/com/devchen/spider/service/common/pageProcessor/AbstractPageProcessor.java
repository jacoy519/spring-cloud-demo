package com.devchen.spider.service.common.pageProcessor;

import com.devchen.spider.enums.SpiderTaskType;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public abstract class AbstractPageProcessor implements PageProcessor {

    public abstract SpiderTaskType getSpiderTaskType();

}
