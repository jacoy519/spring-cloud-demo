package com.devchen.spider.service.biz.zimuzu.handler;

import com.devchen.spider.dal.entity.SpiderTaskEntity;
import com.devchen.spider.service.common.handler.ITaskHandler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ZimuzuSpiderTaskHandler implements ITaskHandler {

    private final static Logger logger = Logger.getLogger(ZimuzuSpiderTaskHandler.class);

    @Override
    public boolean isStatisfied(SpiderTaskEntity spiderTaskEntity) {
        return true;
    }

    @Override
    public boolean doTask(SpiderTaskEntity spiderTaskEntity) {
        logger.info(String.format("run spider task %s", spiderTaskEntity.toString()));
        try {
            Thread.sleep(40000);
        }catch (Exception e) {

        }
        logger.info(String.format("finish spider task %s", spiderTaskEntity.toString()));
        return true;
    }
}
