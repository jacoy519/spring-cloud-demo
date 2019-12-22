package com.devchen.spider.service.common.mgr;

import com.devchen.common.constant.ResultCode;
import com.devchen.spider.enums.SpiderTaskType;
import com.devchen.common.excpetion.FlowException;
import com.devchen.spider.service.common.pageProcessor.AbstractPageProcessor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SpiderPageProcessorManager {

    private final static Logger logger = Logger.getLogger(SpiderPageProcessorManager.class);

    @Autowired
    private List<AbstractPageProcessor> pageProcessorList = new ArrayList<>();


    private Map<SpiderTaskType, AbstractPageProcessor> pageProcessorMap = new HashMap<>();

    @PostConstruct
    private void init() {
        logger.info("[init] start to init the page processor manager");
        for(AbstractPageProcessor pageProcessor : pageProcessorList) {
            if(pageProcessorMap.containsKey(pageProcessor.getSpiderTaskType())) {
                logger.error(String.format("[init] init the page processor manager due to exist the same page processor. spider task type [%s] exec class [%s]",
                        pageProcessor.getSpiderTaskType().name(), pageProcessor.getClass().getName()));
                throw new FlowException(ResultCode.INNER_EXCEPTION);
            }
            pageProcessorMap.put(pageProcessor.getSpiderTaskType(), pageProcessor);
            logger.info(String.format("[init] register page processor. spider task type [%s] exec class [%s]",
                    pageProcessor.getSpiderTaskType().name(), pageProcessor.getClass().getName()));
        }
        logger.info("[init] end to init the page processor manager");
    }


    public AbstractPageProcessor getTargetPageProcessor(SpiderTaskType spiderTaskType) {
        return pageProcessorMap.get(spiderTaskType);
    }
}
