package com.devchen.spider.service.common.handler;

import com.devchen.spider.dal.entity.SpiderTaskEntity;
import com.devchen.spider.enums.SpiderTaskExecResult;
import com.devchen.spider.enums.SpiderTaskType;

public interface ITaskHandler {

    boolean isStatisfied(SpiderTaskEntity spiderTaskEntity);

    boolean doTask(SpiderTaskEntity spiderTaskEntity);
}
