package com.example.higerpoint.controller;

import java.io.File;
import java.util.ArrayList;

/**
 * @author chezhenqi
 * @date 2019/12/30 星期一
 * @time 9:01
 * @description springboot-rabbitmq
 */
public class FileTestController {
    private static ArrayList<String> listname = new ArrayList<String>();

    /*public static void main(String[] args) throws Exception {
        *//**
         * 递归读取文件
         *//*
        readAllFile("data/");
        System.out.println(listname.size());
    }*/

    /**
     * @author chezhenqi
     * @date 9:02 2019/12/30
     * @description: 递归读取文件加下的所有文件
     * @params: No such property: code for class: Script1
     * @return:
     */
    public static void readAllFile(String filepath) {
        File file = new File(filepath);
        if (!file.isDirectory()) {
            listname.add(file.getName());
        } else if (file.isDirectory()) {
            System.out.println("文件");
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(filepath);
                if (!readfile.isDirectory()) {
                    listname.add(readfile.getName());
                } else if (readfile.isDirectory()) {
                    readAllFile(filepath + "\\" + filelist[i]);//递归
                }
            }
        }
        for (int i = 0; i < listname.size(); i++) {
            System.out.println(listname.get(i));
        }
    }
}
