package com.shop3.shop3.controller;

import com.shop3.shop3.dto.CountDto;
import com.shop3.shop3.dto.ItemFormDto;
import com.shop3.shop3.dto.ItemSearchDto;
import com.shop3.shop3.entity.Item;
import com.shop3.shop3.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto",new ItemFormDto());
        return "/item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile")List<MultipartFile> itemImgFileList){
        if (bindingResult.hasErrors()){ // 상품 등록시 필수값이 없다면 다시 상품 등록 페이지로 전환합니다
            return "item/itemForm";
        }

        // 상품 등록시 첫번째 이미지가 없다면 에러 메시지와 함께 상품 등록 페이지로 전환합니다.
        // 상품의 첫번째 이미지는 메인 페이지에서 보여줄 상품 이미지로 사용하기 위해서 필수값으로 지정합니다.
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값입니다");
            return "item/itemForm";
        }

        try { // 상품 저장 로직을 호출합니다. 매개변수로 상품정보와 상품 이미지 정보를 담고있는 itemImgFileList를 넘겨줍니다
            itemService.saveItem(itemFormDto,itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage","상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/"; // 상품이 정상적으로 등록되었다면 메인 페이지로 이동합니다.
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId")Long itemId,Model model){

        try {
            // 조회한 상품 데이터를 모델에 담아서 뷰로 전달합니다
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto",itemFormDto);
        }
        // 상품 엔티티가 존재하지 않을 경우 에러메시지를 담아서 상품 등록페이지로 이동합니다.
        catch (EntityNotFoundException e){
            model.addAttribute("errorMessage","존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto",new ItemFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto,
                             BindingResult bindingResult, @RequestParam("itemImgFile")List<MultipartFile>
                             itemImgFileList,Model model){
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }
        try {
            // 상품 수정로직을 호출합니다.
            itemService.updateItem(itemFormDto,itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage","상품 수정중 에러가 발생했습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }
    // value에 상품 관리 화면 진입시 url에 페이지 번호가 없는 경우와 페이지 번호가 있는경우 2가지를 매핑합니다.
    @GetMapping(value = {"/admin/items","/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page")Optional<Integer> page,Model model){
        //페이징을 위해서 PageRequest.of 메소드를 통해 Pageable 객체를 생성합니다. 첫번째 파라미터로 조회할 페이지 번호, 두번째
        // 파라미터로는 한번에 가지고 올 데이터 수를 넣어줍니다. URL 경로에 페이지 번호가 있으면 해당 페이지를 조회하도록 세팅하고,
        // 페이지 번호가 없으면 0페이지를 조회하도록 합니다.
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,3);

        // 조회 조건과 페이징 정보를 파라미터로 넘겨서 Page<Item>객체를 반환 받습니다.
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto,pageable);
        model.addAttribute("items",items); // 조회한 상품 데이터 및 페이징정보를 뷰에 전달합니다.
        model.addAttribute("itemSearchDto",itemSearchDto);// 페이지 전환시 기존 검색 조건을 유지한채 이동할수있도록 뷰에다시 전달합니다.
        model.addAttribute("maxPage",5);// 상품 관리 메뉴 하단에 보여줄 페이지 번호의 최대 개수입니다. 5로 설정했으므로
                                                                // 최대 5개의 이동할 페이지번호만 보여줍니다.

        return "item/itemMng";
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId")Long itemId, CountDto countDto){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item",itemFormDto);
        model.addAttribute("count",countDto);

        return "item/itemDtl";
    }
}
