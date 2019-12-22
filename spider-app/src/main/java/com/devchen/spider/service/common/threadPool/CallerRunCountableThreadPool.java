package com.devchen.spider.service.common.threadPool;

import org.springframework.stereotype.Component;
import us.codecraft.webmagic.thread.CountableThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CallerRunCountableThreadPool extends CountableThreadPool {


    private int threadNum;

    private AtomicInteger threadAlive = new AtomicInteger();

    private ReentrantLock reentrantLock = new ReentrantLock();

    private Condition condition = reentrantLock.newCondition();

    private CallerRunCountableThreadPool() {
        super(0);
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public int getThreadAlive() {
        return threadAlive.get();
    }

    public int getThreadNum() {
        return threadNum;
    }

    private ExecutorService executorService;

    public void execute(final Runnable runnable) {
        boolean isSelfRun = false;
        for(;;) {
            int val = threadAlive.get();
            if(val >= threadNum) {
                isSelfRun = true;
                break;
            }
            try {
                reentrantLock.lock();
                if(val == threadAlive.get()) {
                    isSelfRun = (val >= threadNum);
                    if(!isSelfRun) {
                        threadAlive.getAndAdd(1);
                    }
                    break;
                }
            } finally {
                reentrantLock.unlock();
            }

        }

        if(isSelfRun) {
            runnable.run();
        } else {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        runnable.run();
                    } finally {
                        threadAlive.getAndAdd(-1);
                    }

                }
            });
        }
    }

    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    public void shutdown() {
        executorService.shutdown();
    }

}
