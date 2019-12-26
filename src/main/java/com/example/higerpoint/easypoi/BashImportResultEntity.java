package com.example.higerpoint.easypoi;

import lombok.Data;

import java.util.List;

/**
 * @author chezhenqi
 * @date 2019/8/6 星期二
 * @time 9:42
 * @description 批量插入数据返回实体
 */
@Data
public class BashImportResultEntity<T> {

    /**
     * 插入结果详情
     */
    private List<T> list;


    /**
     * 返回结果
     */
    private Object data;


}
