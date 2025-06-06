package com.shop3.shop3.dto;

import com.shop3.shop3.constant.ItemSellStatus;
import com.shop3.shop3.entity.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ItemFormDto {

    private Long id;

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String itemDetail;

    private Integer stockNumber;

    private ItemSellStatus itemSellStatus;

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>(); // 상품 저장후 수정할때 상품 이미지 정보를 저장하는 리스트입니다.

    private List<Long> itemImgIds = new ArrayList<>();
    // 상품의 이미지 아이디를 저장하는 리스트입니다. 상품 등록시에는 아직 상품의 이미지를 저장하지 않았기 때문에
    // 아무값도 들어가 있지 않고 수정시에 이미지 아이디를 담아둘 용도로 사용합니다.

    private static ModelMapper modelMapper = new ModelMapper();

    // model mapper를 이용하여 엔티티 객체와 DTO 객체간의 데이터 를 복사하여 복사한 객체를 반환해주는 메소드입니다.
    public Item createItem(){
        return modelMapper.map(this,Item.class);
    }
    public static ItemFormDto of(Item item){
        return modelMapper.map(item, ItemFormDto.class);
    }
}
