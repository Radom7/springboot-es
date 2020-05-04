package com.haiyu.springbootes.dao;

import com.haiyu.springbootes.entity.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @Desc:
 * @Author: liuxing
 * @Date: 2020/5/4 14:37
 * @Version 1.0
 */
@Repository
public interface ArticleReposity extends ElasticsearchRepository<Article,Long> {
}
