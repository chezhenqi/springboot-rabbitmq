package com.example.higerpoint.rabbitmq.producer;

import com.example.higerpoint.elasticsearch.Item;
import com.example.higerpoint.rabbitmq.RabbitConfig;
import com.example.higerpoint.util.JsonUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ItemProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitConfig rabbitConfig;

    public void sendMessageAdd(Item item) {
        rabbitTemplate.convertAndSend(rabbitConfig.getQueueItemAdd(), JsonUtil.object2json(item));
    }

    public void sendMessageUpdate(Item item) {
        rabbitTemplate.convertAndSend(rabbitConfig.getQueueItemUpdate(), JsonUtil.object2json(item));
    }

    public void sendMessageDel(Item item) {
        rabbitTemplate.convertAndSend(rabbitConfig.getQueueItemDel(), JsonUtil.object2json(item));
    }
}
