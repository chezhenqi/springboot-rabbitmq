package com.example.higerpoint.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author chezhenqi
 * @date 2019/12/24 星期二
 * @time 15:35
 * @description higerpoint
 */
@Component
@Slf4j
public class InsureConsumer {

    @RabbitListener(queues = "#{rabbitConfig.getQueueInsureAdd()}")
    public void add(String content) {
        log.info("insureConsumer.add():{}", content);
    }

    @RabbitListener(queues = "#{rabbitConfig.getQueueInsureUpdate()}")
    public void update(String content) {
        log.info("insureConsumer.update():{}", content);
    }

    @RabbitListener(queues = "#{rabbitConfig.getQueueInsureDel()}")
    public void del(String content) {
        log.info("insureConsumer.del():{}", content);
    }
}
