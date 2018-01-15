package com.devchen.article.service;

import com.devchen.article.common.Constant;
import com.devchen.article.common.error.UnionRuntimeException;
import com.devchen.article.common.lock.RedisLockService;
import com.devchen.article.dal.dao.ArticleDao;
import com.devchen.article.dal.entity.ArticleEntity;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.util.internal.ConstantTimeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;

@Service("articleService")
public class ArticleService {

    private final static Logger logger = Logger.getLogger(ArticleService.class);

    @Resource
    private RedisTemplate<String,String> redisCache;

    private final ConcurrentMap<Integer, FutureTask<ArticleEntity>> articleDoFutureCache = new ConcurrentHashMap<Integer, FutureTask<ArticleEntity>>();

    @Resource
    private ThreadPoolTaskExecutor articleTaskExecutor;

    @Autowired
    private RedissonClient redissonClient;

    @Resource
    private ArticleDao articleDao;


    public ArticleEntity getArticleByArticleId(int articleId) {
        FutureTask<ArticleEntity> articleDoFuture = null;
        RLock rLock = getArticleReadLock(articleId);
        try {
            rLock.tryLock(5, TimeUnit.SECONDS);
            articleDoFuture = articleDoFutureCache.get(articleId);
            if(articleDoFuture == null) {
                Callable<ArticleEntity> articleDoCallable = new Callable<ArticleEntity>() {
                    @Override
                    public ArticleEntity call() throws Exception {
                        return doGetDetailArticle(articleId);
                    }
                };
                FutureTask<ArticleEntity> ft = new FutureTask<ArticleEntity>(articleDoCallable);
                articleDoFuture = articleDoFutureCache.putIfAbsent(articleId, ft);
                if(articleDoFuture == null) {
                    articleDoFuture = ft;
                    articleTaskExecutor.submit(ft);
                }
            }
            return articleDoFuture.get();
        } catch (Exception e) {
            logger.error("fail to get aritcle " + articleId, e);
        } finally {
            articleDoFutureCache.remove(articleId,articleDoFuture);
            rLock.unlock();
        }
        return null;
    }

    private ArticleEntity doGetDetailArticle(int articleId) {
        Gson gson = new Gson();
        String redisCacheKey =  Constant.ARTICLE_CACHE_KEY_PREFIX + articleId;
        String articleStr =(String)redisCache.opsForValue().get(redisCacheKey);
        if(!StringUtils.isEmpty(articleStr)) {
            logger.info("get article " + articleId + " from redis cache");
            return gson.fromJson(articleStr,ArticleEntity.class);
        }
        ArticleEntity articleEntity = articleDao.selectArticleByArticleId(articleId);
        if(articleEntity != null) {
            logger.info("get article " + articleId + " from database and save into redis cache");
            redisCache.opsForValue().set(redisCacheKey, gson.toJson(articleEntity), 24, TimeUnit.HOURS);
        }
        return articleEntity;
    }

    public Map<String, Long> getArticleTypeCountMap() {
        RLock rLock = getArticleTypeMapReadLock();
        try {
            rLock.tryLock(5, TimeUnit.SECONDS);
            Gson gson = new Gson();
            String typeMapStr = redisCache.opsForValue().get(Constant.ARTICLE_TYPE_MAP_CACHE_KEY);
            if(StringUtils.isEmpty(typeMapStr)) {
                logger.info("get article type from db");
                List<Map> typeList = articleDao.selectTypeNumberMap();
                Map<String, Long> result = new HashMap<>();
                for(Map map : typeList) {
                    String type = (String)map.get("type");
                    Long count = (Long)map.get("count");
                    result.put(type,count);
                }
                redisCache.opsForValue().set(Constant.ARTICLE_TYPE_MAP_CACHE_KEY, gson.toJson(result), 24, TimeUnit.HOURS);
                return result;
            } else {
                logger.info("get article type from cache");
                return gson.fromJson(typeMapStr, new TypeToken<Map<String, Long>>(){}.getType());
            }
        } catch (Exception e) {
            logger.error("get article type fail", e);
            throw new UnionRuntimeException("9999", "fail to read the type map");
        } finally{
            rLock.unlock();
        }

    }

    public void editArticle(int articleId, ArticleEntity articleEntity) {
        RLock wLock = getArticleWriteLock(articleId);
        RLock typeMapWLock = getArticleTypeMapWriteLock();
        try {
            wLock.tryLock(5, TimeUnit.SECONDS);
            typeMapWLock.tryLock(5, TimeUnit.SECONDS);
            articleEntity.setId(Long.valueOf(articleId));
            int updateValue = articleDao.updateArticle(articleEntity);
            if(updateValue == 0 ) {
                logger.error("not found need updated Article");
                throw new UnionRuntimeException("9999", "update fail");
            }
            String redisCacheKey = Constant.ARTICLE_CACHE_KEY_PREFIX + articleId;
            redisCache.delete(redisCacheKey);
            redisCache.delete(Constant.ARTICLE_TYPE_MAP_CACHE_KEY);
        } catch (Exception e) {
            logger.error("update fail", e);
            throw new UnionRuntimeException("9999", "update fail");
        } finally {
            typeMapWLock.unlock();
            wLock.unlock();
        }
    }

    public void saveNewArticle(ArticleEntity articleEntity) {
        RLock allPageWLock = getAllArticlePageWritleLock();
        RLock typeMapWLock = getArticleTypeMapWriteLock();
        try {
            allPageWLock.tryLock(5, TimeUnit.SECONDS);
            typeMapWLock.tryLock(5, TimeUnit.SECONDS);
            int articleSaveNumber = articleDao.insertArticle(articleEntity);
            if(articleSaveNumber == 0 ) {
                logger.error("not save any article in the db");
                throw new UnionRuntimeException("9999", "save fail");
            }
            redisCache.delete(Constant.ARTICLE_TYPE_MAP_CACHE_KEY);

            String articlePageCacheKeyPattern = Constant.ARTICLE_PAGE_CACHE_KEY_PREFIX + "*";
            Set<String> articlePageCacheKeySet = redisCache.keys(articlePageCacheKeyPattern);
            redisCache.delete(articlePageCacheKeySet);
        } catch (Exception e) {
            logger.error("save fail",e);
            throw new UnionRuntimeException("9999", "save fail");
        } finally {
            typeMapWLock.unlock();
            allPageWLock.unlock();
        }
    }

    public void deleteArticle(int articleId) {
        RLock articleWLock = getArticleWriteLock(articleId);
        RLock typeMapWLock = getArticleTypeMapWriteLock();
        RLock allPageWLock = getAllArticlePageWritleLock();
        try {
            articleWLock.tryLock(5, TimeUnit.SECONDS);
            typeMapWLock.tryLock(5, TimeUnit.SECONDS);
            allPageWLock.tryLock(5, TimeUnit.SECONDS);

            int deleteNumber = articleDao.deleteArticleByArticleId(articleId);
            if(deleteNumber == 0) {
                logger.error("not delete any article in the db");
                throw new UnionRuntimeException("9999", "delete fail");
            }
            String redisCacheKey = Constant.ARTICLE_CACHE_KEY_PREFIX + articleId;
            redisCache.delete(redisCacheKey);
            redisCache.delete(Constant.ARTICLE_TYPE_MAP_CACHE_KEY);

            String articlePageCacheKeyPattern = Constant.ARTICLE_PAGE_CACHE_KEY_PREFIX + "*";
            Set<String> articlePageCacheKeySet = redisCache.keys(articlePageCacheKeyPattern);
            redisCache.delete(articlePageCacheKeySet);
        } catch (Exception e) {
            logger.error("delete fail", e);
            throw new UnionRuntimeException("9999", "delete fail");
        } finally {
            allPageWLock.unlock();
            typeMapWLock.unlock();
            articleWLock.unlock();
        }
    }

    private RLock getArticleReadLock(int articleId) {
        String key = Constant.ARTICLE_RW_LOCK_PREFIX + articleId;
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(key);
        return rwLock.readLock();
    }

    private RLock getArticleWriteLock(int articleId) {
        String key = Constant.ARTICLE_RW_LOCK_PREFIX + articleId;
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(key);

        return rwLock.writeLock();
    }

    private RLock getArticleTypeMapReadLock() {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(Constant.ARTICLE_TYPE_MAP_RW_LOCK);
        return rwLock.readLock();
    }

    private RLock getArticleTypeMapWriteLock() {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(Constant.ARTICLE_TYPE_MAP_RW_LOCK);
        return rwLock.writeLock();
    }

    private RLock getAllArticlePageWritleLock() {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(Constant.ARTICLE_ALL_PAGE_RW_LOCK);
        return rwLock.writeLock();
    }
}
