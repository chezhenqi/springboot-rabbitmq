package com.example.higerpoint.controller;

import com.example.higerpoint.rabbitmq.producer.FinancingProducer;
import com.example.higerpoint.rabbitmq.producer.InsureProducer;
import com.example.higerpoint.rabbitmq.producer.MoneyProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chezhenqi
 * @date 2019/12/24 星期二
 * @time 15:39
 * @description higerpoint
 */
@RestController
public class RabbitTestController {
    @Autowired
    private InsureProducer insureProducer;
    @Autowired
    private MoneyProducer moneyProducer;
    @Autowired
    private FinancingProducer financingProducer;

    /**
     * 测试方法
     *
     * @return
     */
    @GetMapping("/rabbit/testSendMsg")
    public String testSendMsg() {
        insureProducer.sendMessageAdd();
        insureProducer.sendMessageUpdate();
        insureProducer.sendMessageDel();

        moneyProducer.sendMessageAdd();
        moneyProducer.sendMessageUpdate();
        moneyProducer.sendMessageDel();

        financingProducer.sendMessageAdd();
        financingProducer.sendMessageUpdate();
        financingProducer.sendMessageDel();
        return "发送成功！";
    }
}
