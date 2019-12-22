package com.devchen.spider.service.biz.hexun;

import com.devchen.spider.enums.SpiderTaskType;
import com.devchen.spider.service.biz.hexun.handler.HexunTaskHandler;
import com.devchen.spider.service.biz.zimuzu.handler.ZimuzuSpiderTaskHandler;
import com.devchen.spider.service.common.AbstractSpiderTaskService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class HexunSpiderTaskService extends AbstractSpiderTaskService {

    @Resource
    private HexunTaskHandler hexunTaskHandler;

    @PostConstruct
    private void init() {
        registTaskType(SpiderTaskType.HEXUN, hexunTaskHandler);
    }
}
