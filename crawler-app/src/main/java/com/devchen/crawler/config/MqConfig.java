package com.devchen.crawler.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    @Bean
    public ActiveMQQueue noticeMsgQueue() {
        return new ActiveMQQueue("noticeMsg");
    }
}
