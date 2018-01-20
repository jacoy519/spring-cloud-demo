package com.devchen.file.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean
    ThreadPoolTaskExecutor commonMsgHandler() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setKeepAliveSeconds(300);
        return taskExecutor;
    }

    @Bean
    ThreadPoolTaskExecutor errorMsgHandler() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setKeepAliveSeconds(300);
        return taskExecutor;
    }

    @Bean
    ThreadPoolTaskExecutor downloadTaskHandler() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setKeepAliveSeconds(300);
        return taskExecutor;
    }
}
