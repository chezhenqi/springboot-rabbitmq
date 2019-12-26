package com.example.higerpoint.fastdfs;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author chezhenqi
 * @date 2019/12/26 星期四
 * @time 16:13
 * @description springboot-rabbitmq
 */
@Component("fastDFSConfig")
@ConfigurationProperties(prefix = "fastdfs")
@Data
public class FastDFSConfig {
    @Value("${fastdfs.connect_timeout_in_seconds}")
    private String connect_timeout_in_seconds;
    @Value("${fastdfs.network_timeout_in_seconds}")
    private String network_timeout_in_seconds;
    @Value("${fastdfs.http_tracker_http_port}")
    private String http_tracker_http_port;
    @Value("${fastdfs.tracker_servers}")
    private String tracker_servers;
    @Value("${fastdfs.tracker_server_host}")
    private String tracker_server_host;
}
