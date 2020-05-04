package com.haiyu.springbootes;

import com.haiyu.springbootes.dao.PersonRepository;
import com.haiyu.springbootes.entity.Person;
import org.elasticsearch.index.query.Operator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringbootEsApplicationTests {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    //单数据保存
    @Test
    public void save(){
        Person p = new Person(1L,"jack",18,"worker");
        personRepository.save(p);
    }

    //多数据批量保存
    @Test
    public void saveAll(){
        List<Person> personList = new ArrayList<>();

        for (int i = 5; i<10; i++){
            long id = (long)i;
            Person p= new Person(id ,"jason",16+i,"programmer");
            personList.add(p);
        }

        personRepository.saveAll(personList);

    }

    //更新数据
    @Test
    public void update(){
        Person person =  personRepository.queryPersonById(5L);
        System.out.println(person);
        person.setName("Ehson");
        person.setAge(30);
        person.setWork("architects");
        personRepository.save(person);
        System.out.println(personRepository.queryPersonById(5L));
    }

    //根据查询
    @Test
    public void findById(){
        Optional<Person> p = personRepository.findById(1L);
        System.out.println(p);
    }


    //根据name查询
    @Test
    public void findByName(){
        for(Person p: personRepository.findByName("jason")){
            System.out.println(p);
        }
    }

    //查出所有
    @Test
    public void findAll() {
        personRepository.findAll().forEach(p -> System.out.println(p));
    }

    //根据name work查询
    @Test
    public void findByNameAndWork(){
        personRepository.findByNameAndWork("jason","programmer")
                .forEach(p -> System.out.println(p));
    }

    //分页查询
    @Test
    public void testPage(){
        // 分页参数:分页从0开始，age倒序
        Sort sort = Sort.by(Sort.Direction.DESC, "age");
        Pageable pageable = PageRequest.of(0, 5, sort);
        Page<Person> pageageRsutl = personRepository.findByWork("programmer",pageable);
        System.out.println("总数量"+pageageRsutl.getTotalElements());
        System.out.println("总页数"+pageageRsutl.getTotalPages());
        List<Person> personList= pageageRsutl.getContent();//结果
        personList.stream().forEach(p -> System.out.println(p));
    }


    //自定义查询
    @Test
    public void etmTest() {
        //查询关键字
        String word="jason";

        // 分页设置,age倒序
        Pageable pageable =  PageRequest.of(0, 10, Sort.Direction.DESC,"age");
        SearchQuery searchQuery;

        //0.使用queryStringQuery完成单字符串查询queryStringQuery("name", "work")
        //1.multiMatchQuery多个字段匹配 .operator(MatchQueryBuilder.Operator.AND)多项匹配使用and查询即完全匹配都存在才查出来
//        searchQuery = new NativeSearchQueryBuilder().withQuery(multiMatchQuery(word, "name", "work").operator(Operator.AND)).withPageable(pageable).build();

        //2.多条件查询：name和work必须包含word=“XXX”且age必须大于21的以age倒序分页结果
        word="programmer";
        searchQuery = new NativeSearchQueryBuilder().withQuery(boolQuery().must(multiMatchQuery(word, "name", "work").operator(Operator.AND)).must(rangeQuery("age").gt(21))).withPageable(pageable).build();

        List<Person> list= elasticsearchTemplate.queryForList(searchQuery, Person.class);
        list.forEach(p -> System.out.println(p));
    }

}
