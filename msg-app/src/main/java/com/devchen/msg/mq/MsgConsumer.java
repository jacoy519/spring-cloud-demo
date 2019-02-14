package com.devchen.msg.mq;


import com.devchen.msg.service.MsgService;
import org.apache.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MsgConsumer {

    private final static Logger logger = Logger.getLogger(MsgConsumer.class);

    @Resource
    private MsgService msgService;

    @JmsListener(destination = "noticeMsg")
    public void receiveQueue(String msg) {
        logger.info(String.format("receive msg: %s", msg));
        msgService.handleMsg(msg);
    }
}