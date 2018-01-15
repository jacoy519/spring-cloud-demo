package com.devchen.article.resource;


import com.devchen.article.resource.entity.UnionResponse;
import com.devchen.article.resource.factory.UnionResponseFactory;
import com.devchen.article.service.ArticleService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/article-type")
public class ArticleTypeResource {


    private final static Logger logger = Logger.getLogger(ArticleTypeResource.class);

    @Resource
    private ArticleService articleService;

        @RequestMapping(value = "/get-article-type-map", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public UnionResponse getArticleTypeMap() {
            Map<String,Long> articleTypeMap = articleService.getArticleTypeCountMap();
            return UnionResponseFactory.createSuccessResponse(articleTypeMap);
    }
}
