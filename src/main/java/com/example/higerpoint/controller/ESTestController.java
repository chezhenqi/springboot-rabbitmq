package com.example.higerpoint.controller;

import com.example.higerpoint.elasticsearch.Item;
import com.example.higerpoint.elasticsearch.ItemRepository;
import com.example.higerpoint.elasticsearch.ItemSearchDTO;
import com.example.higerpoint.rabbitmq.producer.ItemProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class ESTestController {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemProducer itemProducer;

    /**
     * 添加/更新索引
     *
     * @param item
     */
    @PostMapping("/es/insert")
    public void insert(@RequestBody Item item) {
        itemProducer.sendMessageAdd(item);
    }

    /**
     * 删除索引
     *
     * @param item
     */
    @PostMapping("/es/delete")
    public void delete(@RequestBody Item item) {
        itemProducer.sendMessageDel(item);
    }

    /**
     * 根据id查询
     *
     * @param searchDTO
     * @return
     */
    @PostMapping("/es/findById")
    public Item findById(@RequestBody ItemSearchDTO searchDTO) {
        Item item = null;
        Optional<Item> byId = itemRepository.findById(searchDTO.getId());
        if (byId.isPresent()) {
            item = byId.get();
        }
        return item;
    }

    /**
     * 两个条件模糊查询
     *
     * @param searchDTO
     * @return
     */
    @PostMapping("/es/findByTitleLikeOrBrandLike")
    public List<Item> findByTitleLikeOrBrandLike(@RequestBody ItemSearchDTO searchDTO) {
        return itemRepository.findByTitleLikeOrBrandLike(searchDTO.getSearchStr(), searchDTO.getSearchStr());
    }
}
