package com.shop3.shop3.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class ItemDto {

    private Long id;
    private String itemNm;
    private Integer price;
    private String itemDetail;
    private String sellStatCd;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;
}
