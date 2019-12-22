package com.devchen.spider.service.common.builder;

import com.devchen.spider.enums.SpiderTaskType;
import com.devchen.spider.service.common.entity.SpiderContext;

public interface SpiderContextBuilder {

    SpiderTaskType getSpiderTaskType();

    SpiderContext buildSpiderContext();
}
