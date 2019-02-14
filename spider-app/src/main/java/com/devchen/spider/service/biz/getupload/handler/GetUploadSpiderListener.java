package com.devchen.spider.service.biz.getupload.handler;

import com.devchen.spider.dal.dao.SpiderTaskDAO;
import com.devchen.spider.dal.entity.SpiderTaskEntity;
import com.devchen.spider.enums.SpiderTaskStatus;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.Task;

import javax.annotation.Resource;


public class GetUploadSpiderListener implements SpiderListener {

    private SpiderTaskDAO spiderTaskDAO;

    private Spider spider;

    private SpiderTaskEntity spiderTaskEntity;

    private final static Logger logger = Logger.getLogger(GetUploadSpiderListener.class);

    public GetUploadSpiderListener(SpiderTaskDAO spiderTaskDAO, Spider spider, SpiderTaskEntity spiderTaskEntity) {
        this.spiderTaskDAO = spiderTaskDAO;
        this.spider = spider;
        this.spiderTaskEntity = spiderTaskEntity;
    }


    @Override
    public void onSuccess(Request request) {
        handle();
    }

    @Override
    public void onError(Request request) {
        handle();
    }

    private void handle() {
        SpiderTaskEntity currentTaskEntity = spiderTaskDAO.selectOneById(spiderTaskEntity.getId());
        if(currentTaskEntity.getTaskStatus().equals(SpiderTaskStatus.TERMINATED.name())) {
            logger.info(String.format("the spider %s and task id %s has been forcely ended", spiderTaskEntity.getSpiderId(), spiderTaskEntity.getId().toString()));
            spider.stop();
        } else {
            spiderTaskDAO.updateUpdatedAt(currentTaskEntity.getId());
        }
    }
}
