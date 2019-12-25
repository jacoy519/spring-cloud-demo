package com.devchen.common.task.mgr;

import com.devchen.common.task.constant.TaskJob;
import com.devchen.common.task.constant.TaskStatus;
import com.devchen.common.task.constant.TaskType;
import com.devchen.common.task.dao.AbstractTaskDAO;
import com.devchen.common.task.dto.AbstractTaskDTO;
import com.devchen.common.task.endpoint.TaskWorkerEndPoint;
import com.devchen.common.task.handler.TaskHandler;
import com.devchen.common.task.loader.TaskHandlerFactory;
import com.devchen.spider.enums.SpiderTaskStatus;
import com.devchen.spider.service.common.SpiderConfigService;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDistributeTaskManager<T extends AbstractTaskDTO> extends AbstractTaskManager<T> {


}
