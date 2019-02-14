package com.devchen.crawler.service;

import com.hundsun.message.interfaces.IH5Session;
import com.hundsun.message.interfaces.IH5SessionSettings;
import com.hundsun.message.interfaces.IUserOperationCallback;
import com.hundsun.message.net.HsH5Session;
import com.hundsun.message.net.HsSessionManager;
import com.hundsun.message.net.NetworkAddr;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MqService {

    @Autowired
    private JmsMessagingTemplate jmsTemplate;

    @Resource
    private ActiveMQQueue noticeMsgQueue;

    public static void main(String[] args) throws Exception{
        IH5Session session = HsSessionManager.createSession("test");
        String appKey =  "88a605c6-4ce0-45b1-9f10-99c1254c29f4";
        String appSecert = "a0ae3977-bbfc-4aac-8b93-42f6386dab08";
        String quoteURL = "hq.hscloud.cn";
        int quotePost = 9999;

        IH5SessionSettings settings = session.getSessionSettings();
        List<NetworkAddr> list = new ArrayList<>();
        NetworkAddr networkAddr = new NetworkAddr();
        networkAddr.setServerIP(quoteURL);
        networkAddr.setServerPort(quotePost);
        networkAddr.setServerName(quoteURL);
        list.add(networkAddr);

        settings.setNetworkAddrList(list);
        settings.setTemplatePath("/root");
        settings.setQueueSize(100);
        settings.setClientType(HsH5Session.CLIENT_TYPE_PC);
        settings.setAppKey(appKey);
        settings.setAppSecret(appSecert);
        session.loginByUser("guest", "guest", new IUserOperationCallback() {
            @Override
            public void onResponse(HashMap<String, String> hashMap, IH5Session ih5Session) {
                int i=0;
            }
        });
    }

    public void sendMsgToNoticeMsgQueue(String msg) {
        this.jmsTemplate.convertAndSend(noticeMsgQueue, msg);
    }
}
