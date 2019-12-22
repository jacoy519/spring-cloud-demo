package com.devchen.spider.service.common.handler;

import com.devchen.spider.dal.entity.SpiderTaskEntity;
import com.devchen.spider.enums.SpiderTaskType;
import com.devchen.spider.service.biz.zimuzu.handler.ZimuzuPageProcessor;
import com.devchen.spider.service.biz.zimuzu.handler.ZimuzuPipeline;
import com.devchen.spider.service.common.SpiderConfigService;
import com.devchen.spider.service.common.entity.SpiderContext;
import com.devchen.spider.service.common.mgr.SpiderContextManager;
import com.devchen.spider.service.common.mgr.SpiderPageProcessorManager;
import com.devchen.spider.service.common.spider.SpiderRedisScheduler;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;

@Component
public class ExecutorTaskHandler implements ITaskHandler{

    @Resource
    private SpiderRedisScheduler spiderRedisScheduler;

    @Resource
    private SpiderConfigService spiderConfigService;

    @Resource
    private SpiderContextManager spiderContextManager;

    private final static Logger logger = Logger.getLogger(ExecutorTaskHandler.class);


    @Override
    public boolean isStatisfied(SpiderTaskEntity spiderTaskEntity) {
        return true;
    }

    @Override
    public boolean doTask(SpiderTaskEntity spiderTaskEntity) {

        SpiderTaskType spiderTaskType = getSpiderTaskType(spiderTaskEntity);

        SpiderContext spiderContext = spiderContextManager.getContext(spiderTaskType);

        Spider spider = Spider.create(spiderContext.getPageProcessor());
        spider.setUUID(spiderTaskEntity.getSpiderId());

        for(Pipeline pipeline : spiderContext.getPipelineList()) {
            spider.addPipeline(pipeline);
        }

        for(Request request : spiderContext.getStartRequestList()) {
            spider.addRequest(request);
        }


        spider.setExitWhenComplete(false);

        spider.run();

        return true;
    }


    private SpiderTaskType getSpiderTaskType(SpiderTaskEntity spiderTaskEntity) {
        return SpiderTaskType.valueOf(spiderTaskEntity.getTaskType());
    }
}
