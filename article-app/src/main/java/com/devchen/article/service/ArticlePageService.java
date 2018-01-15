package com.devchen.article.service;

import com.devchen.article.common.Constant;
import com.devchen.article.dal.dao.ArticleDao;
import com.devchen.article.dal.entity.ArticleEntity;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("articlePageService")
public class ArticlePageService {

    private final static Logger logger = Logger.getLogger(ArticlePageService.class);
    @Resource
    private RedisTemplate<String,String> redisCache;

    @Resource
    private ArticleDao articleDao;

    @Autowired
    private RedissonClient redissonClient;

    @Resource
    private ArticleService articleService;


    public PageInfo<ArticleEntity> getDetailArticlePage(int pageNum) {
        String pageIndexRedisCacheKey = Constant.ARTICLE_PAGE_CACHE_KEY_PREFIX + pageNum;
        String pageIndexStr = (String)redisCache.opsForValue().get(pageIndexRedisCacheKey);
        PageInfo<Integer> articleIndexPage = null;
        if(StringUtils.isEmpty(pageIndexStr)) {
            logger.info("load from db");
            RLock articlePageReadLock = getAllArticlePageReadLock();
            RLock aricleIndexPageLock = getArticlePageIndexLock(String.valueOf(pageNum));
            try {
                articlePageReadLock.tryLock(5, TimeUnit.SECONDS);
                aricleIndexPageLock.tryLock(5, TimeUnit.SECONDS);
                pageIndexStr = (String)redisCache.opsForValue().get(pageIndexRedisCacheKey);
                if(StringUtils.isEmpty(pageIndexStr)) {
                    PageHelper.startPage(pageNum, Constant.ARTICLE_PAGE_ITEM_NUM);
                    List<Integer> articleIdList = articleDao.selectAllArticleId();
                    articleIndexPage = new PageInfo<>(articleIdList);
                    redisCache.opsForValue().set(pageIndexRedisCacheKey, new Gson().toJson(articleIndexPage));
                }
            } catch (Exception e) {
                logger.error(e);
            }
            finally {
                aricleIndexPageLock.unlock();
                articlePageReadLock.unlock();
            }

        } else {
            logger.info("load from cache");
            articleIndexPage = (new Gson()).fromJson(pageIndexStr,  new TypeToken<PageInfo<Integer>>(){}.getType());
        }
        return convertIndexPageToArticlePage(articleIndexPage);
    }

    public PageInfo<ArticleEntity> getDetailArticlePageByType(int pageNum, String type) {
        String pageIndexRedisCacheKey = Constant.ARTICLE_PAGE_CACHE_KEY_PREFIX + pageNum + type;
        String pageIndexStr = (String)redisCache.opsForValue().get(pageIndexRedisCacheKey);
        PageInfo<Integer> articleIndexPage = null;
        if(StringUtils.isEmpty(pageIndexStr)) {
            logger.info("load from db");
            RLock articlePageReadLock = getAllArticlePageReadLock();
            RLock aricleIndexPageLock = getArticlePageIndexLock(String.valueOf(pageNum) + type);
            try {
                articlePageReadLock.tryLock(5, TimeUnit.SECONDS);
                aricleIndexPageLock.tryLock(5, TimeUnit.SECONDS);
                pageIndexStr = (String)redisCache.opsForValue().get(pageIndexRedisCacheKey);
                if(StringUtils.isEmpty(pageIndexStr)) {
                    PageHelper.startPage(pageNum, Constant.ARTICLE_PAGE_ITEM_NUM);
                    List<Integer> articleIdList = articleDao.selectAllArticleIdByType(type);
                    articleIndexPage = new PageInfo<>(articleIdList);
                    redisCache.opsForValue().set(pageIndexRedisCacheKey, new Gson().toJson(articleIndexPage));
                }
            } catch (Exception e) {
                logger.error(e);
            }
            finally {
                aricleIndexPageLock.unlock();
                articlePageReadLock.unlock();
            }

        } else {
            logger.info("load from cache");
            articleIndexPage = (new Gson()).fromJson(pageIndexStr,  new TypeToken<PageInfo<Integer>>(){}.getType());
        }
        return convertIndexPageToArticlePage(articleIndexPage);
    }

    public PageInfo<ArticleEntity> getDetailArticlePageBySearchKey(int pageNum, String searchKey) {
        String pageIndexRedisCacheKey = Constant.ARTICLE_PAGE_CACHE_KEY_PREFIX + pageNum + "search" +searchKey;
        String pageIndexStr = (String)redisCache.opsForValue().get(pageIndexRedisCacheKey);
        PageInfo<Integer> articleIndexPage = null;
        if(StringUtils.isEmpty(pageIndexStr)) {
            logger.info("load from db");
            RLock articlePageReadLock = getAllArticlePageReadLock();
            RLock aricleIndexPageLock = getArticlePageIndexLock(String.valueOf(pageNum) + "search"  + searchKey);
            try {
                articlePageReadLock.tryLock(5, TimeUnit.SECONDS);
                aricleIndexPageLock.tryLock(5, TimeUnit.SECONDS);
                pageIndexStr = (String)redisCache.opsForValue().get(pageIndexRedisCacheKey);
                if(StringUtils.isEmpty(pageIndexStr)) {
                    PageHelper.startPage(pageNum, Constant.ARTICLE_PAGE_ITEM_NUM);
                    List<Integer> articleIdList = articleDao.selectAllArticleIdBySearchKey(searchKey);
                    articleIndexPage = new PageInfo<>(articleIdList);
                    redisCache.opsForValue().set(pageIndexRedisCacheKey, new Gson().toJson(articleIndexPage));
                }
            } catch (Exception e) {
                logger.error(e);
            }
            finally {
                aricleIndexPageLock.unlock();
                articlePageReadLock.unlock();
            }

        } else {
            logger.info("load from cache");
            articleIndexPage = (new Gson()).fromJson(pageIndexStr,  new TypeToken<PageInfo<Integer>>(){}.getType());
        }
        return convertIndexPageToArticlePage(articleIndexPage);
    }

    private RLock getAllArticlePageReadLock() {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(Constant.ARTICLE_ALL_PAGE_RW_LOCK);
        return rwLock.readLock();
    }

    private RLock getArticlePageIndexLock(String pageKey) {
        return redissonClient.getFairLock(Constant.ARTICLE_PAGE_INDEX_RW_LOCK + pageKey);
    }

    private PageInfo<ArticleEntity> convertIndexPageToArticlePage(PageInfo<Integer>  articleIndexPage) {
        List<Integer> articleIndexList = articleIndexPage.getList();
        List<ArticleEntity> articleEntities = new ArrayList<>();
        for(Integer id : articleIndexList) {
            ArticleEntity articleEntity = articleService.getArticleByArticleId(id);
            if(articleEntity != null) {
                articleEntities.add(articleEntity);
            }
        }
        PageInfo<ArticleEntity> articleEntityPageInfo = new PageInfo<>(articleEntities);
        articleEntityPageInfo.setHasNextPage(articleIndexPage.isHasNextPage());
        articleEntityPageInfo.setHasPreviousPage(articleIndexPage.isHasPreviousPage());
        articleEntityPageInfo.setIsFirstPage(articleIndexPage.isIsFirstPage());
        articleEntityPageInfo.setIsLastPage(articleIndexPage.isIsLastPage());
        articleEntityPageInfo.setPrePage(articleIndexPage.getPrePage());
        articleEntityPageInfo.setNextPage(articleIndexPage.getNextPage());
        articleEntityPageInfo.setFirstPage(articleIndexPage.getFirstPage());
        articleEntityPageInfo.setLastPage(articleIndexPage.getLastPage());
        return articleEntityPageInfo;
    }
}
