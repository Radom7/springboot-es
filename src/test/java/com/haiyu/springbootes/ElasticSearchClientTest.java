package com.haiyu.springbootes;

import com.google.gson.Gson;
import com.haiyu.springbootes.entity.Article;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;

/**
 * @Desc: ElasticSearchClient
 * @Author: liuxing
 * @Date: 2020/5/4 10:03
 * @Version 1.0
 */
public class ElasticSearchClientTest {

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

    @Test
    public void createIndex() throws Exception{
        //创建一个Settings对象，相当于一个配置信息，主要用于配置集群名称
        Settings settings = Settings.builder()
                .put("cluster.name", "my-application")
                .put("thread_pool.search.size", thread_pool)
                .build();
        //创建一个客户端Client对象
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddresses(new TransportAddress(InetAddress.getByName(host), 9301));
        client.addTransportAddresses(new TransportAddress(InetAddress.getByName(host), 9302));
        client.addTransportAddresses(new TransportAddress(InetAddress.getByName(host),9303));
        //使用client对象创建一个索引库
        client.admin().indices().prepareCreate("index_hello").get();
        //关闭client对象
        client.close();
    }


    @Test
    public void setMapping() throws Exception{
        Settings settings = Settings.builder()
                .put("cluster.name", "my-application")
                .put("thread_pool.search.size", thread_pool)
                .build();
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddresses(new TransportAddress(InetAddress.getByName(host), 9301));
        client.addTransportAddresses(new TransportAddress(InetAddress.getByName(host), 9302));
        client.addTransportAddresses(new TransportAddress(InetAddress.getByName(host),9303));

        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("article")
                        .startObject("properties")
                            .startObject("id")
                                .field("type","long")
                                .field("store",true)
                            .endObject()
                            .startObject("title")
                                .field("type","text")
                                .field("store",true)
                                .field("analyzer","ik_smart")
                            .endObject()
                            .startObject("content")
                                .field("type","text")
                                .field("store",true)
                                .field("analyzer","ik_smart")
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject();

        //创建映射
        client.admin().indices()
                //设置要做映射的索引
                .preparePutMapping("index_hello")
                //设置要做映射的type
                .setType("article")
                //mapping信息 XContentBuilder对象或者是json格式字符串
                .setSource(builder)
                //执行操作
                .get();

        client.close();
    }

    @Test
    public void testAddDocumentByXContentBuilder() throws Exception{
        //创建一个文档对象
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                    .field("id",2L)
                    .field("title","c++报道")
                    .field("content","我是一名教师，来自英国")
                .endObject();
        //把文档对象添加到索引库
        client.prepareIndex()
                //设置文档名称
                .setIndex("index_hello")
                //设置type
                .setType("article")
                //设置文档id,如果不设置的话自动生成一个id
                .setId("2")
                //设置文档信息
                .setSource(builder)
                //执行操作
                .get();
        //关闭客户端
        client.close();
    }

    @Test
    public void testAddDocumentByJson() throws Exception{
        Article article = new Article();
        article.setId(3L);
        article.setTitle("画家报道");
        article.setContent("我是一名画家，来自法国");
        //转成json
        String json = new Gson().toJson(article);
        client.prepareIndex("index_hello","article","3")
                .setSource(json, XContentType.JSON)
                .get();
    }

    @Test
    public void testAddDocumentByJson2() throws Exception{
        for (int i = 4; i < 100; i++) {
            Article article = new Article();
            article.setId(i);
            article.setTitle("画家报道"+i);
            article.setContent("我是一名画家，来自法国");
            //转成json
            String json = new Gson().toJson(article);
            client.prepareIndex("index_hello","article", String.valueOf(i))
                    .setSource(json, XContentType.JSON)
                    .get();
        }
    }
}
