package com.haiyu.springbootes.dao;

import com.haiyu.springbootes.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Desc:
 * @Author: liuxing
 * @Date: 2020/5/3 20:30
 * @Version 1.0
 */
@Repository
public interface PersonRepository extends ElasticsearchRepository<Person,Long> {
    Person queryPersonById(Long id);

    List<Person> findByName(String name);

    List<Person> findByNameAndWork(String name, String work);

    Page<Person> findByWork(String work, Pageable pageable);
}
