package com.example.higerpoint.rabbitmq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.example.higerpoint.elasticsearch.Item;
import com.example.higerpoint.elasticsearch.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ItemConsumer {

    @Autowired
    private ItemRepository itemRepository;

    @RabbitListener(queues = "#{rabbitConfig.getQueueItemAdd()}")
    public void add(String content) {
        itemRepository.save(JSONObject.parseObject(content, Item.class));
    }

    @RabbitListener(queues = "#{rabbitConfig.getQueueItemUpdate()}")
    public void update(String content) {
        itemRepository.save(JSONObject.parseObject(content, Item.class));
    }

    @RabbitListener(queues = "#{rabbitConfig.getQueueItemDel()}")
    public void del(String content) {
        itemRepository.delete(JSONObject.parseObject(content, Item.class));
    }
}
