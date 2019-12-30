package com.example.higerpoint.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * es实现了一部分API
 * 参考：https://blog.csdn.net/chen_2890/article/details/83895646
 */
public interface ItemRepository extends ElasticsearchRepository<Item, Long> {
    List<Item> findByTitleLikeOrBrandLike(String title,String brand);
}
