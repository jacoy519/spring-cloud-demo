package com.devchen.article.resource;

import com.devchen.article.dal.entity.ArticleEntity;
import com.devchen.article.resource.entity.UnionResponse;
import com.devchen.article.resource.factory.UnionResponseFactory;
import com.devchen.article.service.ArticlePageService;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/article-page")
public class ArticlePageResource {

    private final static Logger logger = Logger.getLogger(ArticlePageService.class);

    @Resource
    private ArticlePageService articlePageService;

    @RequestMapping(value = "/get-article-home-page", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public UnionResponse getArticleHomePage(@RequestParam("pageNo") int pageNo) {
        PageInfo<ArticleEntity> page = articlePageService.getDetailArticlePage(pageNo);
        return UnionResponseFactory.createSuccessResponse(page);
    }

    @RequestMapping(value = "/get-article-type-page", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public UnionResponse getArticleTypePage(@RequestParam("pageNo") int pageNo, @RequestParam("type") String type) {
        PageInfo<ArticleEntity> page = articlePageService.getDetailArticlePageByType(pageNo, type);
        return UnionResponseFactory.createSuccessResponse(page);
    }

    @RequestMapping(value = "/get-article-search-page", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public UnionResponse getArticleSearchPage(@RequestParam("pageNo") int pageNo, @RequestParam("searchKey") String searchKey) {
        PageInfo<ArticleEntity> page = articlePageService.getDetailArticlePageBySearchKey(pageNo, searchKey);
        return UnionResponseFactory.createSuccessResponse(page);
    }
}
