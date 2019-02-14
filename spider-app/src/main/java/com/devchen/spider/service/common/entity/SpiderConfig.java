package com.devchen.spider.service.common.entity;

public class SpiderConfig {

    private long delayTime = 60L * 60L * 1000L;

    private long executorMaxRunTime = 10L * 60L * 1000L;

    private long monitorMaxRunTime = 100L * 1000L;

    private int threadNum = 1;

    private long emptyWaitTime = 10L * 1000L;
}
