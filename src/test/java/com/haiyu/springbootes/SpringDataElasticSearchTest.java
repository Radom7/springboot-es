package com.haiyu.springbootes;

import com.haiyu.springbootes.dao.ArticleReposity;
import com.haiyu.springbootes.entity.Article;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Desc:
 * @Author: liuxing
 * @Date: 2020/5/4 14:43
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringDataElasticSearchTest {
    @Autowired
    private ArticleReposity articleReposity;

    @Autowired
    private ElasticsearchTemplate template;

    //创建索引库
    @Test
    public void createIndex(){
        //创建索引，并配置映射关系
        template.createIndex(Article.class);
        //配置映射关系
//        template.putMapping(Article.class);
    }

    //添加文档
    @Test
    public void addDocument(){
        for (int i = 10; i <= 20; i++) {
            Article article = new Article();
            article.setId(i);
            article.setTitle("小伙在火车站失踪，险些被骗至内蒙古放羊！ "+i);
            article.setContent("身无分文的聋哑人，希望坐火车到外地寻找亲人；等待接站的叔叔，发现侄子在北京西站下车后不知所踪，竟被骗子“忽悠”要去内蒙放羊。记者上午获悉，4月中下旬以来，北京市公安局公交总队民警帮助多名弱势群体的乘客，与家人团聚。");
            articleReposity.save(article);
        }
    }

    //删除文档
    @Test
    public void deleteDocumentById(){
        articleReposity.deleteById(2L);
        //全部删除
//        articleReposity.deleteAll();
    }

    @Test
    public void findAll(){
        Iterable<Article> articles = articleReposity.findAll();
        articles.forEach(a -> System.out.println(a));
    }

    //原生查询
    @Test
    public void search(){
        //创建一个查询对象
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery("小伙在火车站").defaultField("title"))
                .withPageable(PageRequest.of(0,15))
                .build();
        //执行查询
        List<Article> articles = template.queryForList(query, Article.class);
        articles.forEach(a -> System.out.println(a));
    }
}
