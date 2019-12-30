package com.example.higerpoint.elasticsearch;

import lombok.Data;

@Data
public class ItemSearchDTO {
    private Long id;
    private String searchStr;
    private Integer pageNum;
    private Integer pageSize;
}
