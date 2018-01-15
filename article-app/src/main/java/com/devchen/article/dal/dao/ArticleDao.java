package com.devchen.article.dal.dao;

import com.devchen.article.dal.entity.ArticleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleDao {

    ArticleEntity selectArticleByArticleId(@Param("articleId") int articleId);

    List<ArticleEntity> selectAllArticle();

    List<Map> selectTypeNumberMap();

    List<Integer> selectAllArticleId();

    int deleteArticleByArticleId(@Param("articleId") int articleId);

    int updateArticle(@Param("article") ArticleEntity article);

    int insertArticle(@Param("article") ArticleEntity article);

    List<Integer> selectAllArticleIdByType(@Param("articleType") String articleType);

    List<Integer> selectAllArticleIdBySearchKey(@Param("searchKey") String searchKey);

}