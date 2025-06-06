package com.shop3.shop3.repository;

import com.shop3.shop3.dto.ItemSearchDto;
import com.shop3.shop3.dto.MainItemDto;
import com.shop3.shop3.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    // 상품 조회 조건을 담고있는 itemSearchDto 객체와 페이징 정보를 담고있는 Pageable 객체를 파라미터로 받는
    // getAdminItemPage 메소드를 정의합니다. 반환 데이터로 Page<Item> 객체를 반환합니다.
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto,Pageable pageable);
}
