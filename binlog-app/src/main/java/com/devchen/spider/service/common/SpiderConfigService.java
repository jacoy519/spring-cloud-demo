package com.devchen.spider.service.common;

import com.devchen.spider.dal.dao.SpiderTaskConfigDAO;
import com.devchen.spider.dal.entity.SpiderTaskConfigEntity;
import com.devchen.spider.enums.SpiderTaskType;
import com.devchen.spider.enums.SpiderTimeModel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SpiderConfigService {

    @Resource
    private SpiderTaskConfigDAO spiderTaskConfigDAO;

    private final static String CONFIG_TIME_MODEL_KEY = "TIME_MODEL";

    private final static String CKN_SPIDER_EXECUTOR_THREAD_NUM = "SPIDER_EXECUTOR_THREAD_NUM";

    private final static long DEFAULT_SPIDER_EXECUTOR_THREAD_NUM = 3;

    private final static SpiderTimeModel DEFAULT_TIME_MODEL = SpiderTimeModel.HALF_HOUR;

    public SpiderTimeModel getSpiderTimeModel(SpiderTaskType spiderTaskType) {
        SpiderTaskConfigEntity spiderTaskConfigEntity = spiderTaskConfigDAO.selectByGroupNameAndKeyName(
                spiderTaskType.name(),
                CONFIG_TIME_MODEL_KEY
        );
        if(spiderTaskConfigEntity == null) {
            return DEFAULT_TIME_MODEL;
        }
        return SpiderTimeModel.valueOf(spiderTaskConfigEntity.getKeyValue());
    }


    public long getExecutorThreadNum(SpiderTaskType spiderTaskType) {
        SpiderTaskConfigEntity spiderTaskConfigEntity = spiderTaskConfigDAO.selectByGroupNameAndKeyName(
                spiderTaskType.name(),
                CKN_SPIDER_EXECUTOR_THREAD_NUM
        );
        if(spiderTaskConfigEntity == null) {
            return DEFAULT_SPIDER_EXECUTOR_THREAD_NUM;
        }
        return Long.valueOf(spiderTaskConfigEntity.getKeyValue());
    }
}
