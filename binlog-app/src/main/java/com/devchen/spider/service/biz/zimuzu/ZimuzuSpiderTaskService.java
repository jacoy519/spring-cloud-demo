package com.devchen.spider.service.biz.zimuzu;

import com.devchen.spider.enums.SpiderTaskType;
import com.devchen.spider.service.biz.zimuzu.handler.ZimuzuSpiderTaskHandler;
import com.devchen.spider.service.common.mgr.AbstractSpiderTaskManager;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


@Service
public class ZimuzuSpiderTaskService extends AbstractSpiderTaskManager{

    @Resource
    private ZimuzuSpiderTaskHandler zimuzuSpiderTaskHandler;

    @PostConstruct
    private void init() {
        registTaskType(SpiderTaskType.ZIMUZU_FAV_SPIDER, zimuzuSpiderTaskHandler);
    }

}
