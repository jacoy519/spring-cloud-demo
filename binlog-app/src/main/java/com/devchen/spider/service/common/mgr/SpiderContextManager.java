package com.devchen.spider.service.common.mgr;

import com.devchen.common.constant.ResultCode;
import com.devchen.common.excpetion.FlowException;
import com.devchen.spider.enums.SpiderTaskType;
import com.devchen.spider.service.common.builder.SpiderContextBuilder;
import com.devchen.spider.service.common.entity.SpiderContext;
import com.devchen.spider.service.common.pageProcessor.AbstractPageProcessor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class SpiderContextManager {

    private final static Logger logger = Logger.getLogger(SpiderPageProcessorManager.class);

    @Autowired
    private List<SpiderContextBuilder> builderList = new ArrayList<>();

    private Map<SpiderTaskType, SpiderContextBuilder> builderMap = new HashMap<>();

    @PostConstruct
    private void init() {
        logger.info("[init] start to init the spider context manager");
        for(SpiderContextBuilder builder : builderList) {
            if(builderMap.containsKey(builder.getSpiderTaskType())) {
                logger.error(String.format("[init] init the spider context manager due to exist the same spider context. spider task type [%s] exec class [%s]",
                        builder.getSpiderTaskType().name(), builder.getClass().getName()));
                throw new FlowException(ResultCode.INNER_EXCEPTION);
            }
            builderMap.put(builder.getSpiderTaskType(), builder);
            logger.info(String.format("[init] register spider context. spider task type [%s] exec class [%s]",
                    builder.getSpiderTaskType().name(), builder.getClass().getName()));
        }
        logger.info("[init] end to init the spider context manager");
    }


    public SpiderContext getContext(SpiderTaskType spiderTaskType) {
        return builderMap.get(spiderTaskType).buildSpiderContext();
    }


}
