package com.devchen.spider.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean
    ThreadPoolTaskExecutor spiderExecutorPool() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setKeepAliveSeconds(300);
        return taskExecutor;
    }

    @Bean
    ThreadPoolTaskExecutor spiderMoniterPool() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setKeepAliveSeconds(300);
        return taskExecutor;
    }


    @Bean
    ThreadPoolTaskExecutor downloadPool() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3);
        taskExecutor.setKeepAliveSeconds(300);
        return taskExecutor;
    }

}
