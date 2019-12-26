package com.example.higerpoint.rabbitmq.producer;

import com.example.higerpoint.rabbitmq.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author chezhenqi
 * @date 2019/12/24 星期二
 * @time 15:28
 * @description higerpoint
 */
@Component
public class FinancingProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitConfig rabbitConfig;

    public void sendMessageAdd() {
        rabbitTemplate.convertAndSend(rabbitConfig.getQueueFinancingAdd(), "financingAdd");
    }

    public void sendMessageUpdate() {
        rabbitTemplate.convertAndSend(rabbitConfig.getQueueFinancingUpdate(), "financingUpdate");
    }

    public void sendMessageDel() {
        rabbitTemplate.convertAndSend(rabbitConfig.getQueueFinancingDel(), "financingDel");
    }
}
