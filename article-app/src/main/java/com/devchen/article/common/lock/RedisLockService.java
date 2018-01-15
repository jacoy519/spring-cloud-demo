package com.devchen.article.common.lock;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * Created by medivh on 2017/9/14.
 */
@Service("redisLockService")
public class RedisLockService{

    private final static  Logger logger = Logger.getLogger(RedisLockService.class);

    @Resource
    private RedisTemplate<String,String> redisCache;

    public boolean acquireLockWithTimeOut(String name, long time) {
        String key = "key:" + String.valueOf(Thread.currentThread().getId());
        String lockName = "lockName:" + name;
        while(true) {
            if(saveNX(lockName, key)) {
                expire(lockName, time);
                return true;
            } else if (ttl(key)>0) {
                expire(lockName, time);
            }
            try {
                Thread.sleep(30);
            } catch (Exception e) {

            }

        }
    }

    public boolean acquireLock(String key) {
        String saveKey = "key:" + String.valueOf(Thread.currentThread().getId());
        String lockName = "lockName:" + key;
        while(true) {
            if(saveNX(lockName, saveKey)) {
                return true;
            }
            try {
                Thread.sleep(30);
            } catch (Exception e) {

            }
        }
    }

    public boolean releaseLock(final String name) {
        String key = "key:" + String.valueOf(Thread.currentThread().getId());
       return  redisCache.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                String lockName = "lockName:" + name;
                while (true) {
                    try {
                        redisConnection.watch(lockName.getBytes());
                        byte[] bytes=redisConnection.get(lockName.getBytes());
                        if(Arrays.equals(redisConnection.get(lockName.getBytes()), key.getBytes())) {
                            redisConnection.multi();
                            redisCache.delete(lockName);
                            redisConnection.exec();
                            return Boolean.TRUE;
                        }
                        redisConnection.unwatch();
                        break;
                    }catch (Exception e) {
                        logger.error(e);
                    }
                }
                return Boolean.FALSE;
            }
        });
    }

    public boolean acquireReadLock(String key) {
        while(true) {
            acquireLock(key);
            Double writer = redisCache.opsForZSet().score("readWriteZset","writer:"+key);
            Double requireWriter = redisCache.opsForZSet().score("readWriteZset","requireWriter:"+key);
            if((writer ==null || writer < 1.0 )&& ( requireWriter ==null || requireWriter < 1.0)) {
                redisCache.opsForZSet().incrementScore("readWriteZset","reader:"+key,1.0);
                releaseLock(key);
                return true;
            }
            releaseLock(key);
            try {
                Thread.sleep(30);
            } catch (Exception e) {

            }
        }
    }


    public boolean releaseReadLock(String key) {
        redisCache.opsForZSet().incrementScore("readWriteZset","reader:"+key,-1.0);
        return true;
    }


    public boolean acquireWriteLock(String key) {
        redisCache.opsForZSet().incrementScore("readWriteZset","requireWriter:"+key,1.0);
        while(true) {
            acquireLock(key);
            Double writer = redisCache.opsForZSet().score("readWriteZset","writer:"+key);
            Double reader = redisCache.opsForZSet().score("readWriteZset","reader:"+key);
            if((writer == null || writer < 1.0) && (reader == null || reader < 1.0)) {
                redisCache.opsForZSet().incrementScore("readWriteZset","requireWriter:"+key,-1.0);
                redisCache.opsForZSet().incrementScore("readWriteZset","writer:"+key,1.0);
                releaseLock(key);
                return true;
            }
            releaseLock(key);
            try {
                Thread.sleep(30);
            } catch (Exception e) {

            }
        }

    }


    public boolean releaseWriteLock(String key) {
        redisCache.opsForZSet().incrementScore("readWriteZset","writer:"+key,-1.0);
        return false;
    }

    private boolean saveNX(String key, String val) {
        return redisCache.execute((RedisCallback<Boolean>) connection -> {
            return connection.setNX(key.getBytes(), val.getBytes());
        });
    }

    public void expire(String key, long seconds) {
        redisCache.execute((RedisCallback<Boolean>) connection -> connection.expire(key.getBytes(), seconds));
    }

    public long ttl(String key) {
        return redisCache.execute((RedisCallback<Long>) connection -> {
           return connection.ttl(key.getBytes());
        });
    }
}
