package com.devchen.spider.service.biz.weixin.lcFunds;

import com.devchen.spider.enums.SpiderTaskType;
import com.devchen.spider.service.biz.weixin.lcFunds.handler.LcFundsTaskHandler;
import com.devchen.spider.service.common.AbstractSpiderTaskService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class LcFundsSpiderTaskService extends AbstractSpiderTaskService {

    @Resource
    private LcFundsTaskHandler lcFundsTaskHandler;

    @PostConstruct
    private void init() {
        registTaskType(SpiderTaskType.WEIXIN_LC_FUNDS, lcFundsTaskHandler);
    }
}
