package com.shop3.shop3.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CartOrderDto {
    private Long cartItemId;

    private List<CartOrderDto> cartOrderDtoList; // 장바구니에서 여러개의 상품을 주문하므로 CartDto 클래스가 자기 자신을 List로 가지고있도록 합니다.
}
