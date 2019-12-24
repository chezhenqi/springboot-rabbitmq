package com.example.higerpoint.producer;

import com.example.higerpoint.config.RabbitConfig;
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
public class InsureProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitConfig rabbitConfig;

    public void sendMessageAdd() {
        rabbitTemplate.convertAndSend(rabbitConfig.getQueueInsureAdd(), "insureAdd");
    }

    public void sendMessageUpdate() {
        rabbitTemplate.convertAndSend(rabbitConfig.getQueueInsureUpdate(), "insureUpdate");
    }

    public void sendMessageDel() {
        rabbitTemplate.convertAndSend(rabbitConfig.getQueueInsureDel(), "insureDel");
    }
}
