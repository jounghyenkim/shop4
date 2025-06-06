package com.shop3.shop3.dto;

import com.shop3.shop3.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter @Setter
public class ItemImgDto {

    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYn;

    private static ModelMapper modelMapper =new ModelMapper();
    // 멤버 변수로 ModelMapper 객체를 추가합니다.

    public static ItemImgDto of(ItemImg itemImg){
        return modelMapper.map(itemImg,ItemImgDto.class);
    }// ItemImg 엔티티 객체를 파라미터로 받아서 ITemImg 객체의 자료형과 멤버변수의 이름이 같을때
    // ItemImgDto 로 값을 복사해서 반환합니다. static 메소드로 선언해 ItemImgDto 객체를 생성하지 않아도 호출할수있도록 합니다.
}
