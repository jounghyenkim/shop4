package com.shop3.shop3.controller;

import com.shop3.shop3.dto.CartDetailDto;
import com.shop3.shop3.dto.CartItemDto;
import com.shop3.shop3.dto.CartOrderDto;
import com.shop3.shop3.service.CartService;
import com.shop3.shop3.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final MemberService memberService;

    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, HttpSession httpSession, Model model){
        String email = memberService.loadMemberEmail(principal,httpSession);
        List<CartDetailDto> cartDetailDtoList = cartService.getCartList(email);
        model.addAttribute("cartItems",cartDetailDtoList);
        return "cart/cartList";
    }


    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                                              BindingResult bindingResult, Principal principal, HttpSession httpSession){
        if (bindingResult.hasErrors()){ // 장바구니에 담을 상품 정보를 받는 cartItemDto 객체에 데이터 바인딩 시 에러가있는지 검사합니다.
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        String email = memberService.loadMemberEmail(principal,httpSession); // 현재 로그인한 회원의 이메일 정보를 변수에 저장합니다.
        Long cartItemId;
        try {
            cartItemId= cartService.addCart(cartItemDto,email); // 화면으로부터 넘어온 장바구니에 담을 상품 정보와 현재 로그인한 회원의 이메일
        }catch (Exception e){                                   // 정보를 이용하여 장바구니에 상품을 담는 로직입니다.
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(cartItemId,HttpStatus.OK); // 결과값으로 생성된 장바구니 상품 아이디와 요청이 성공하였다는 HTTP 응답상태코드를 반환합니다.
    }

    @PatchMapping(value = "/cartItem/{cartItemId}") // HTTP 메소드에서 PATCH 는 요청된 자원의 일부를 업데이트할때 PATCH 를 사용합니다. 장바구니 상품의 수량만 업데이트하기때문에 사용합니다.
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId")Long cartItemId,int count,Principal principal,
                                                       HttpSession httpSession){
        String email = memberService.loadMemberEmail(principal,httpSession);
        if (count<0){ // 장바구니에 담겨있는 상품의 개수를 0개 이하로 업데이트 요청을 할때 에러 메시지를 담아서 반환합니다.
            return new ResponseEntity<String>("최소 1개 이상 담아주세요",HttpStatus.BAD_REQUEST);
        } else if (!cartService.validateCartItem(cartItemId,email)) { // 수정 권한을 체크합니다
            return new ResponseEntity<String>("수정 권한이 없습니다.",HttpStatus.FORBIDDEN);
        }
        cartService.updateCartItemCount(cartItemId,count); // 장바구니 상품의 개수를 업데이트합니다.
        return new ResponseEntity<Long>(cartItemId,HttpStatus.OK);
    }
    @DeleteMapping(value = "/cartItem/{cartItemId}")// Http 메소드에서 DELETE 인 경우 요청된 자원을 삭제할때 사용합니다.
    public @ResponseBody ResponseEntity deleteCartITem(@PathVariable("cartItemId")Long cartItemId,Principal principal,
                                                       HttpSession httpSession){
        String email = memberService.loadMemberEmail(principal,httpSession);
        if (!cartService.validateCartItem(cartItemId,email)){ // 수정권한을 체크합니다
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        cartService.deleteCartItem(cartItemId); // 해당 장바구니 상품을 삭제합니다.
        return new ResponseEntity<Long>(cartItemId,HttpStatus.OK);

    }
    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem (@RequestBody CartOrderDto cartOrderDto,Principal principal,
                                                       HttpSession httpSession){
        String email = memberService.loadMemberEmail(principal,httpSession);
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
        if (cartOrderDtoList == null || cartOrderDtoList.size() == 0){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요",HttpStatus.FORBIDDEN);
        }
        for (CartOrderDto cartOrderDto1 : cartOrderDtoList){
            if (!cartService.validateCartItem(cartOrderDto1.getCartItemId(),email)){
                return new ResponseEntity<String>("주문 권한이 없습니다.",HttpStatus.FORBIDDEN);
            }
        }
        Long orderId = cartService.orderCartItem(cartOrderDtoList,email);
        return new ResponseEntity<Long>(orderId,HttpStatus.OK);
    }

}
