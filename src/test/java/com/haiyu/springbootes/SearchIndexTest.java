package com.haiyu.springbootes;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;

/**
 * @Desc:
 * @Author: liuxing
 * @Date: 2020/5/4 11:39
 * @Version 1.0
 */
@Slf4j
public class SearchIndexTest {

    private String host = "192.168.163.131";

    private Integer thread_pool = 5;

    private TransportClient client;

    @Before
    public void init() throws Exception{
        //创建一个Settings对象，相当于一个配置信息，主要用于配置集群名称
        Settings settings = Settings.builder()
                .put("cluster.name", "my-application")
                .put("thread_pool.search.size", thread_pool)
                .build();
        //创建一个客户端Client对象
        client = new PreBuiltTransportClient(settings);
        client.addTransportAddresses(new TransportAddress(InetAddress.getByName(host), 9301));
        client.addTransportAddresses(new TransportAddress(InetAddress.getByName(host), 9302));
        client.addTransportAddresses(new TransportAddress(InetAddress.getByName(host),9303));
    }

    @After
    public void close(){
        //关闭client对象
        client.close();
    }

    private void search(QueryBuilder queryBuilder) throws Exception{
        SearchResponse searchResponse = client.prepareSearch("index_hello")
                .setTypes("article")
                .setQuery(queryBuilder)
                .get();
        //取查询结果
        SearchHits searchHits = searchResponse.getHits();
        //取查询结果的总记录数
        log.info("查询结果的总记录数:"+ searchHits.getTotalHits().value);
        //查询结果列表
        Iterator<SearchHit> iterator = searchHits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit = iterator.next();
            //打印文档对象，以json格式输出
            log.info(searchHit.getSourceAsString());
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap.get("id"));
            System.out.println((String) sourceAsMap.get("title"));
            System.out.println((String) sourceAsMap.get("content"));
        }
    }

    //分页
    private void search(QueryBuilder queryBuilder,int from,int size) throws Exception{
        SearchResponse searchResponse = client.prepareSearch("index_hello")
                .setTypes("article")
                .setQuery(queryBuilder)
                //设置分页信息
                .setFrom(from)
                //每一页显示的行数
                .setSize(size)
                .get();
        //取查询结果
        SearchHits searchHits = searchResponse.getHits();
        //取查询结果的总记录数
        log.info("查询结果的总记录数:"+ searchHits.getTotalHits().value);
        //查询结果列表
        Iterator<SearchHit> iterator = searchHits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit = iterator.next();
            //打印文档对象，以json格式输出
            log.info(searchHit.getSourceAsString());
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap.get("id"));
            System.out.println((String) sourceAsMap.get("title"));
            System.out.println((String) sourceAsMap.get("content"));
        }
    }

    //高亮
    private void search(QueryBuilder queryBuilder,String highliggtField) throws Exception{
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //高亮显示的字段
        highlightBuilder.field(highliggtField);
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");

        SearchResponse searchResponse = client.prepareSearch("index_hello")
                .setTypes("article")
                .setQuery(queryBuilder)
                //设置分页信息
                .setFrom(0)
                //每一页显示的行数
                .setSize(5)
                //设置高亮信息
                .highlighter(highlightBuilder)
                .get();
        //取查询结果
        SearchHits searchHits = searchResponse.getHits();
        //取查询结果的总记录数
        log.info("查询结果的总记录数:"+ searchHits.getTotalHits().value);
        //查询结果列表
        Iterator<SearchHit> iterator = searchHits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit = iterator.next();
            //打印文档对象，以json格式输出
            log.info(searchHit.getSourceAsString());
            System.out.println("-------文档属性----------");
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap.get("id"));
            System.out.println((String) sourceAsMap.get("title"));
            System.out.println((String) sourceAsMap.get("content"));
            System.out.println("********高亮结果*******");
            //高亮结果
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            System.out.println(highlightFields);
            //取title高亮显示的结果
            HighlightField field = highlightFields.get(highliggtField);
            Text[] fragments = field.getFragments();
            if(fragments != null){
                String title = fragments[0].toString();
                System.out.println(title);
            }
        }
    }

    //根据id查询
    @Test
    public void searchById() throws Exception{
        //创建一个查询对象
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds("1","2");
        search(queryBuilder);
    }

    //根据Term查询（关键词）
    @Test
    public void searchByTerm() throws Exception{
        //创建一个QueryBuilder对象
        QueryBuilder queryBuilder = QueryBuilders.termQuery("title","画家");
        search(queryBuilder);
    }

    //QueryString查询
    @Test
    public void QueryString() throws Exception{
        //创建一个QueryBuilder对象
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("新加坡的工程师");
        search(queryBuilder);

    }

    @Test
    public void QueryString2() throws Exception{
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("新加坡的画家");
        //分页查询
        search(queryBuilder,0,5);
        //高亮查询
//        search(queryBuilder,"title");
    }


}
