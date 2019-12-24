package com.example.higerpoint.consumer;

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
public class MoneyConsumer {

    @RabbitListener(queues = "#{rabbitConfig.getQueueMoneyAdd()}")
    public void add(String content) {
        log.info("moneyConsumer.add():{}", content);
    }

    @RabbitListener(queues = "#{rabbitConfig.getQueueMoneyUpdate()}")
    public void update(String content) {
        log.info("moneyConsumer.update():{}", content);
    }

    @RabbitListener(queues = "#{rabbitConfig.getQueueMoneyDel()}")
    public void del(String content) {
        log.info("moneyConsumer.del():{}", content);
    }
}
