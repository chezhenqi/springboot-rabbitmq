package com.example.higerpoint.rabbitmq;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author chezhenqi
 * @date 2019/12/24 星期二
 * @time 14:12
 * @description higerpoint
 */
@Data
@Configuration(value = "rabbitConfig")
public class RabbitConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    /**
     * 消息队列名
     */
    @Value("${properties.queue.insure.add}")
    private String queueInsureAdd;
    @Value("${properties.queue.insure.update}")
    private String queueInsureUpdate;
    @Value("${properties.queue.insure.del}")
    private String queueInsureDel;
    @Value("${properties.queue.money.add}")
    private String queueMoneyAdd;
    @Value("${properties.queue.money.update}")
    private String queueMoneyUpdate;
    @Value("${properties.queue.money.del}")
    private String queueMoneyDel;
    @Value("${properties.queue.financing.add}")
    private String queueFinancingAdd;
    @Value("${properties.queue.financing.update}")
    private String queueFinancingUpdate;
    @Value("${properties.queue.financing.del}")
    private String queueFinancingDel;
    @Value("${properties.queue.item.add}")
    private String queueItemAdd;
    @Value("${properties.queue.item.update}")
    private String queueItemUpdate;
    @Value("${properties.queue.item.del}")
    private String queueItemDel;

    /**
     * 交换空间名称
     */
    public static final String EXCHANGE = "microboot.exchange";

    /**
     * 设置路由key
     */
    public static final String ROUTINGKEY = "microboot.routingkey";

    /**
     * @author chezhenqi
     * @date 14:42 2019/12/24
     * @description: 创建mq连接
     * @param:
     * @return:
     */
    @Bean(name = "connectionFactory")
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    /**
     * @author chezhenqi
     * @date 16:36 2019/12/24
     * @description: 创建交换空间
     * @params: No such property: code for class: Script1
     * @return:
     */
    @Bean
    public DirectExchange exchange() { // 使用直连的模式
        return new DirectExchange(EXCHANGE, true, true);
    }

    /**
     * @author chezhenqi
     * @date 16:36 2019/12/24
     * @description: 创建队列
     * @params: No such property: code for class: Script1
     * @return:
     */
    @Bean
    public Binding bindingAueueInsureAdd() {
        return BindingBuilder.bind(queueInsureAdd()).to(exchange()).with(ROUTINGKEY);
    }

    @Bean
    public Binding bindingQueueInsureUpdate() {
        return BindingBuilder.bind(queueInsureUpdate()).to(exchange()).with(ROUTINGKEY);
    }

    @Bean
    public Binding bindingQueueInsureDel() {
        return BindingBuilder.bind(queueInsureDel()).to(exchange()).with(ROUTINGKEY);
    }

    @Bean
    public Binding bindingQueueMoneyAdd() {
        return BindingBuilder.bind(queueMoneyAdd()).to(exchange()).with(ROUTINGKEY);
    }

    @Bean
    public Binding bindingQueueMoneyUpdate() {
        return BindingBuilder.bind(queueMoneyUpdate()).to(exchange()).with(ROUTINGKEY);
    }

    @Bean
    public Binding bindingQueueMoneyDel() {
        return BindingBuilder.bind(queueMoneyDel()).to(exchange()).with(ROUTINGKEY);
    }

    @Bean
    public Binding bindingQueueFinancingAdd() {
        return BindingBuilder.bind(queueFinancingAdd()).to(exchange()).with(ROUTINGKEY);
    }

    @Bean
    public Binding bindingQueueFinancingUpdate() {
        return BindingBuilder.bind(queueFinancingUpdate()).to(exchange()).with(ROUTINGKEY);
    }

    @Bean
    public Binding bindingQueueFinancingDel() {
        return BindingBuilder.bind(queueFinancingDel()).to(exchange()).with(ROUTINGKEY);
    }

    @Bean
    public Binding bindingQueueItemAdd() {
        return BindingBuilder.bind(queueItemAdd()).to(exchange()).with(ROUTINGKEY);
    }

    @Bean
    public Binding bindingQueueItemUpdate() {
        return BindingBuilder.bind(queueItemUpdate()).to(exchange()).with(ROUTINGKEY);
    }

    @Bean
    public Binding bindingQueueItemDel() {
        return BindingBuilder.bind(queueItemDel()).to(exchange()).with(ROUTINGKEY);
    }


    /**
     * 将需要创建的消息队列实例化
     */
    @Bean
    public Queue queueInsureAdd() {
        return new Queue(queueInsureAdd, true);
    }

    @Bean
    public Queue queueInsureUpdate() {
        return new Queue(queueInsureUpdate, true);
    }

    @Bean
    public Queue queueInsureDel() {
        return new Queue(queueInsureDel, true);
    }

    @Bean
    public Queue queueMoneyAdd() {
        return new Queue(queueMoneyAdd, true);
    }

    @Bean
    public Queue queueMoneyUpdate() {
        return new Queue(queueMoneyUpdate, true);
    }

    @Bean
    public Queue queueMoneyDel() {
        return new Queue(queueMoneyDel, true);
    }

    @Bean
    public Queue queueFinancingAdd() {
        return new Queue(queueFinancingAdd, true);
    }

    @Bean
    public Queue queueFinancingUpdate() {
        return new Queue(queueFinancingUpdate, true);
    }

    @Bean
    public Queue queueFinancingDel() {
        return new Queue(queueFinancingDel, true);
    }

    @Bean
    public Queue queueItemAdd() {
        return new Queue(queueItemAdd, true);
    }

    @Bean
    public Queue queueItemUpdate() {
        return new Queue(queueItemUpdate, true);
    }

    @Bean
    public Queue queueItemDel() {
        return new Queue(queueItemDel, true);
    }

}
