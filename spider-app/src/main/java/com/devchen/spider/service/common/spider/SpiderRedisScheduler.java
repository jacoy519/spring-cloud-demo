package com.devchen.spider.service.common.spider;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


@Component
public class SpiderRedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {

    @Resource
    private RedisTemplate<String,String> redisCache;

    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";

    @PostConstruct
    private void init() {
        this.setDuplicateRemover(this);
    }


    public long getLeftRequestCount(String uuid) {
        return redisCache.opsForList().size(uuid);
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        return Integer.valueOf(redisCache.opsForList().size(getQueueKey(task.getUUID())).toString());
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        return redisCache.opsForSet().add(getSetKey(task.getUUID()),request.getUrl()) == 0;
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        redisCache.delete(getSetKey(task.getUUID()));
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return Integer.valueOf(redisCache.opsForSet().size(getSetKey(task.getUUID())).toString());
    }

    @Override
    public Request poll(Task task) {
        String url = redisCache.opsForList().leftPop(getQueueKey(task.getUUID()));
        if (url == null) {
            return null;
        }
        String key = getItemKey(task.getUUID());
        String field = DigestUtils.shaHex(url);
        Object object = redisCache.opsForHash().get(key, field);
        if (object != null) {
            Request o = JSON.parseObject(object.toString(), Request.class);
            return o;
        }
        Request request = new Request(url);
        return request;

    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        redisCache.opsForList().rightPush(getQueueKey(task.getUUID()), request.getUrl());
        if(request.getExtras()!= null) {
            String field = DigestUtils.shaHex(request.getUrl());
            String value = JSON.toJSONString(request);
            redisCache.opsForHash().put(getItemKey(task.getUUID()),field, value);
        }
    }


    public void cleanRedis(String uuid) {
        redisCache.delete(getItemKey(uuid));
        redisCache.delete(getQueueKey(uuid));
        redisCache.delete(getSetKey(uuid));
    }



    protected String getSetKey(String uuid) {
        return SET_PREFIX + uuid;
    }

    protected String getQueueKey(String uuid) {
        return QUEUE_PREFIX + uuid;
    }

    protected String getItemKey(String uuid)
    {
        return ITEM_PREFIX + uuid;
    }
}
