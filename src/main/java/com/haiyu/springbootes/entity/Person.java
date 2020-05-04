package com.haiyu.springbootes.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

/**
 * @Desc:
 * @Author: liuxing
 * @Date: 2020/5/3 20:28
 * @Version 1.0
 */
@Data
@Document(indexName="data",type="person")
public class Person implements Serializable {
    private Long id;
    private String name;
    private Integer age;
    private String work;

    public Person() {
    }

    public Person(Long id, String name, Integer age, String work) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.work = work;
    }
}
