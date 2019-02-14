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

    private final static SpiderTimeModel DEFAULT_TIME_MODEL = SpiderTimeModel.ONE_MIN;

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
}
