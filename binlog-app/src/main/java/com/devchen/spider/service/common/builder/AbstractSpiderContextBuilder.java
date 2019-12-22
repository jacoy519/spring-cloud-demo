package com.devchen.spider.service.common.builder;

import com.devchen.common.constant.ResultCode;
import com.devchen.common.excpetion.FlowException;
import com.devchen.spider.enums.SpiderTaskType;
import com.devchen.spider.service.common.entity.SpiderContext;
import com.devchen.spider.service.common.mgr.SpiderPageProcessorManager;
import org.apache.catalina.Pipeline;
import org.apache.log4j.Logger;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.annotation.Resource;
import java.util.List;


public abstract class AbstractSpiderContextBuilder implements SpiderContextBuilder {

    @Resource
    private SpiderPageProcessorManager spiderPageProcessorManager;

    protected abstract List<Request> getStartRequestList();

    protected abstract List<Pipeline> getPipelineList();

    private final static Logger logger = Logger.getLogger(AbstractSpiderContextBuilder.class);

    @Override
    public SpiderContext buildSpiderContext() {

        SpiderContext spiderContext = new SpiderContext();

        PageProcessor pageProcessor = spiderPageProcessorManager.getTargetPageProcessor(getSpiderTaskType());
        if(pageProcessor == null) {
            logger.error(String.format("[buildSpiderContext] find empty page processor for spider task %s", getSpiderTaskType().name()));
            throw new FlowException(ResultCode.INNER_EXCEPTION);
        }

        spiderContext.setPageProcessor(pageProcessor);
        List<Pipeline> pipelineList = getPipelineList();
        if(pipelineList == null || pipelineList.isEmpty()) {
            logger.error(String.format("[buildSpiderContext] get empty pipeline list for spider task %s", getSpiderTaskType().name()));
            throw new FlowException(ResultCode.INNER_EXCEPTION);
        }
        spiderContext.setPipelineList(pipelineList);

        List<Request> requestList = getStartRequestList();
        if(requestList == null || requestList.isEmpty()) {
            logger.error(String.format("[buildSpiderContext] get empty request list for spider task %s", getSpiderTaskType().name()));
            throw new FlowException(ResultCode.INNER_EXCEPTION);
        }
        spiderContext.setStartRequestList(requestList);


        return spiderContext;
    }
}
