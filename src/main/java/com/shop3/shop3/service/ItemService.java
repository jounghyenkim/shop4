package com.shop3.shop3.service;

import com.shop3.shop3.dto.ItemFormDto;
import com.shop3.shop3.dto.ItemImgDto;
import com.shop3.shop3.dto.ItemSearchDto;
import com.shop3.shop3.dto.MainItemDto;
import com.shop3.shop3.entity.Item;
import com.shop3.shop3.entity.ItemImg;
import com.shop3.shop3.repository.ItemImgRepository;
import com.shop3.shop3.repository.ItemRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto,
                         List<MultipartFile> itemImgFileList) throws Exception{

        // 상품 등록
        Item item = itemFormDto.createItem(); // 상품 등록 폼으로부터 입력 받은 데이터를 이용하여 item 객체를 생성합니다.
        itemRepository.save(item); // 상품 데이터를 저장합니다.

        // 이미지 등록
        for (int i = 0; i<itemImgFileList.size();i++){
            ItemImg itemImg =new ItemImg();
            itemImg.setItem(item);

            if (i == 0){ // 첫번째 이미지 일경우 대표 상품 이미지 여부 값을 Y로 세팅합니다. 나머지 상품이미지는 N으로 설정합니다.
                itemImg.setRepimgYn("Y");
            }
            else{
                itemImg.setRepimgYn("N");

            }
            itemImgService.saveItemImg(itemImg,itemImgFileList.get(i)); // 상품이미지 정보를 저장합니다.
        }
        return item.getId();
    }

    // 상품 데이터를 읽어오는 트랜잭션을 읽기 전용을 설정합니다. 이럴 경우 JPA 가 더티체킹(변경감지)울 수행하지
    // 않아서 성능을 향상시킬수 있습니다.
    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId){

        // 해당 상품의 이미지를 조회합니다 등록순으로 가지고 오기위해 상품 이미지 아이디 오름차순으로 가지고 오겠습니다.
        List<ItemImg> itemImgList =
                itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();

        // 조회한 itemImg 엔티티를 ItemImgDto 객체로 만들어서 리스트에 추가합니다.
        for (ItemImg itemImg: itemImgList){
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        // 상품의 아이디를 통해 상품 엔티티를 조회합니다. 존재하지 않을때는 EntityNotFoundException 을 발생시킵니다.
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);

        return itemFormDto;
    }
    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto,
                                       Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto,pageable);
    }
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto,
                                             Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto,pageable);
    }


    public Long updateItem(ItemFormDto itemFormDto,
                           List<MultipartFile> itemImgFileList) throws Exception{
            // 상품 수정
        Item item = itemRepository.findById(itemFormDto.getId()) // 상품 등록 화면으로부터 전달받은 상품 아이디를 이용하여 상품엔티티를 조회합니다.
                .orElseThrow(EntityNotFoundException::new);

        // 상품 등록화면으로 부터 전달 받은 itemFormDto 를 통해 상품 엔티티를 업데이트합니다.
        item.updateItem(itemFormDto);

        // 상품 이미지 아이디 리스트를 조회합니다.
        List<Long> itemImgIds = itemFormDto.getItemImgIds();
        // 이미지 등록
        for (int i=0; i<itemImgFileList.size(); i++){
            // 상품 이미지를 업데이트 하기위해서 updateItemImg() 메소드에 상품 이미지 아이디와, 상품 이미지 파일정보를 파라미터로 전달합니다.
            itemImgService.updateImg(itemImgIds.get(i),itemImgFileList.get(i));
        }
        return item.getId();
    }
}
