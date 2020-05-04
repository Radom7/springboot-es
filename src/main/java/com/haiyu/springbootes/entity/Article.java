package com.haiyu.springbootes.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @Desc:
 * @Author: liuxing
 * @Date: 2020/5/4 11:22
 * @Version 1.0
 */
@Data
@Document(indexName = "index_blog",type = "article")
public class Article {
    @Id
    @Field(type = FieldType.Long,store = true)
    private long id;
    @Field(type = FieldType.Text,store = true,analyzer = "ik_smart")
    private String title;
    @Field(type = FieldType.Text,store = true,analyzer = "ik_smart")
    private String content;
}
