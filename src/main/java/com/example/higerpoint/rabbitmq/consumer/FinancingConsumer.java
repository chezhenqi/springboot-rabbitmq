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
public class FinancingConsumer {

    @RabbitListener(queues = "#{rabbitConfig.getQueueFinancingAdd()}")
    public void add(String content) {
        log.info("financingConsumer.add():{}", content);
    }

    @RabbitListener(queues = "#{rabbitConfig.getQueueFinancingUpdate()}")
    public void update(String content) {
        log.info("financingConsumer.update():{}", content);
    }

    @RabbitListener(queues = "#{rabbitConfig.getQueueFinancingDel()}")
    public void del(String content) {
        log.info("financingConsumer.del():{}", content);
    }
}
