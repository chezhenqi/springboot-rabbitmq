package com.example.higerpoint.thread;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chezhenqi
 * @date 2019/12/27 星期五
 * @time 10:56
 * @description springboot-rabbitmq
 */
public class ThreadLocal {
    public static void main(String[] args) {
        try {
            method("fsdjal");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void method(String url) throws InterruptedException, IOException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        /**
         * true 表示将文件追加到原文件末尾
         */
        FileWriter writer = new FileWriter(new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + url + ".log", true);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            Thread.sleep(1);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName() + "：" + index + "：" + url);
                        writer.append(Thread.currentThread().getName() + "：" + index + "：" + url + "\n");
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
