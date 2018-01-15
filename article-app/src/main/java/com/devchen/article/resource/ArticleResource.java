package com.devchen.article.resource;


import com.devchen.article.dal.entity.ArticleEntity;
import com.devchen.article.resource.entity.UnionResponse;
import com.devchen.article.resource.factory.UnionResponseFactory;
import com.devchen.article.service.ArticleService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.events.Event;

import javax.annotation.Resource;

@RestController
@RequestMapping("/article")
public class ArticleResource {

    private final static Logger logger = Logger.getLogger(ArticleResource.class);

    @Resource
    private ArticleService articleService;

    @RequestMapping(value = "/get-article", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public UnionResponse getArticle(@RequestParam("id") int id) {
        logger.info("get article: " + id);
        ArticleEntity articleEntity = articleService.getArticleByArticleId(id);
        return UnionResponseFactory.createSuccessResponse(articleEntity);
    }

    @RequestMapping(value = "/create-new-article", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public UnionResponse createNewArticle(@RequestBody ArticleEntity article) {
        logger.info("create article");
        articleService.saveNewArticle(article);
        return UnionResponseFactory.createSuccessResponse("create success");
    }

    @RequestMapping(value = "/edit-article", method = RequestMethod.PUT, produces = "application/json; charset=utf-8")
    public UnionResponse editArticle(@RequestParam("id") int id, @RequestBody ArticleEntity article) {
        logger.info("edit article" + id);
        articleService.editArticle(id, article);
        return UnionResponseFactory.createSuccessResponse("update success");
    }

    @RequestMapping(value = "/edit-article", method = RequestMethod.DELETE, produces = "application/json; charset=utf-8")
    public UnionResponse deleteArticle(@RequestParam("id") int id) {
        logger.info("create article");
        articleService.deleteArticle(id);
        return UnionResponseFactory.createSuccessResponse("delete success");
    }

}
