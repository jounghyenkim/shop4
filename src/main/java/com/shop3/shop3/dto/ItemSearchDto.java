package com.shop3.shop3.dto;

import com.shop3.shop3.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemSearchDto {

    private String searchDateType; // 현재시간과 상품 등록일을 비교하여 상품 데이터를 조회합니다.

    private ItemSellStatus searchSellStatus; // 상품의 판매상태를 기준으로 상품데이터를 조회합니다

    private String searchBy; // 상품을 조회할때 어떤 유형으로 조회할지 선택합니다. : itemNm: 상품명, createdBy: 상품 등록자 아이디

    private String searchQuery=""; // 조회할 검색어 저장할 변수입니다. searchBy가 itemNm 일 경우 상품명을 기준으로 검색하고
                                    // createdBy일 경우 상품 등록자 아이디 기준으로 검색합니다.
}
