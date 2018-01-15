package com.devchen.article.dao;

import com.devchen.article.dal.dao.ArticleDao;
import com.devchen.article.dal.entity.ArticleEntity;
import com.devchen.article.service.ArticlePageService;
import com.devchen.article.service.ArticleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ArticleDaoTest {

    private final static Logger logger = Logger.getLogger(ArticleDao.class);
    @Resource
    private ArticleService articleService;

    @Resource
    private ArticleDao articleDao;

    @Resource
    private ArticlePageService articlePageService;

    @Test
    public void testGetArticleSearchKeyPage() {
        PageInfo<ArticleEntity> test = articlePageService.getDetailArticlePageBySearchKey(1 ,"spring");
        PageInfo<ArticleEntity> test2 = articlePageService.getDetailArticlePageBySearchKey(1 ,"spring");
        int i=0;
    }

    //@Test
    public void testGetArticleTypePage() {
        PageInfo<ArticleEntity> test = articlePageService.getDetailArticlePageByType(1 ,"Java");
        PageInfo<ArticleEntity> test2 = articlePageService.getDetailArticlePageByType(1, "Java");
        articleService.deleteArticle(254);
        PageInfo<ArticleEntity> test3 = articlePageService.getDetailArticlePageByType(1, "Java");
        int i=0;
    }

    //@Test
    public void testGetArticlePage() {
        PageInfo<ArticleEntity> test = articlePageService.getDetailArticlePage(1);
        PageInfo<ArticleEntity> test2 = articlePageService.getDetailArticlePage(1);
        int i=0;
    }

    //@Test
    public void testGetArticleId() {
        PageHelper.startPage(5, 10);
        List<Integer> test = new ArrayList<Integer>();
        PageInfo<Integer> test2 = new PageInfo<>(test);
        int i=0;
    }

    //@Test
    public void testGetArticle() {
        ArticleEntity articleEntity = articleService.getArticleByArticleId(149);
         int i=0;
    }

    //@Test
    public void testGetArticleTypeMap() {
        Map<String, Long> map = articleService.getArticleTypeCountMap();
        articleService.getArticleTypeCountMap();
    }

    //@Test
    public void testSaveArticle() {
        ArticleEntity articleEntity = articleService.getArticleByArticleId(149);
        articleEntity.setContent("112314123");
        articleService.saveNewArticle(articleEntity);
        articleService.getArticleTypeCountMap();
    }

   // @Test
    public void getArticleService() {
        ArticleEntity articleEntity = articleService.getArticleByArticleId(149);
        articleEntity.setContent("112314123");
        articleService.editArticle(149, articleEntity);
        articleService.getArticleByArticleId(149);
    }

    //@Test
    public void deleteArticleService() {
        articleService.getArticleByArticleId(255);
        articleService.deleteArticle(255);
    }
}
