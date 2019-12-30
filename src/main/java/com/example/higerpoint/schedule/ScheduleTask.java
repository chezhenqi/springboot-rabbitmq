package com.example.higerpoint.schedule;

import com.example.higerpoint.elasticsearch.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 */
@Component
public class ScheduleTask {

    /**
     * elasticSearch连接类
     */
    @Autowired
    private ElasticsearchTemplate esTemplate;

    /**
     * 2分钟执行一次
     */
    @Scheduled(cron = "* 0/2 * * * ? ")
    public void createIndex() {
        esTemplate.createIndex(Item.class);
    }
}
