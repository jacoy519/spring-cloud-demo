package com.devchen.spider.service.biz.getupload;

import com.devchen.spider.enums.SpiderTaskType;
import com.devchen.spider.service.biz.getupload.handler.GetUploadSpiderTaskHandler;
import com.devchen.spider.service.common.AbstractSpiderTaskService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


@Service
public class GetUploadSpiderTaskService extends AbstractSpiderTaskService{

    @Resource
    private GetUploadSpiderTaskHandler getUploadSpiderTaskHandler;

    @PostConstruct
    private void init() {
        registTaskType(SpiderTaskType.GETUPLOAD_FAV, getUploadSpiderTaskHandler);
    }

}
