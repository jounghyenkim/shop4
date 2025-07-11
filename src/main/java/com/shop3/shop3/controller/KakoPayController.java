package com.shop3.shop3.controller;

import com.shop3.shop3.dto.*;
import com.shop3.shop3.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;


@Controller
@RequestMapping(value = "/kakao")
@RequiredArgsConstructor

public class KakoPayController {

private final KakaoPayService kakaoPayService;

private final ItemService itemService;

private final MemberService memberService;

private final OrderService orderService;

private final CartService cartService;
    // 카카오페이결제 요청
    @GetMapping("/pay/{itemId}")
    public @ResponseBody ReadyResponse payReady(@PathVariable("itemId")Long itemId,OrderDto orderDto,HttpSession httpSession) {
        // 카카오 결제 준비하기	- 결제요청 service 실행
        httpSession.setAttribute("orderDto",orderDto);
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        ReadyResponse readyResponse = kakaoPayService.payReady(itemFormDto,orderDto);

        return readyResponse; // 클라이언트에 보냄.(tid,next_redirect_pc_url이 담겨있음.)

    }
//    @GetMapping("/pay")
//    public @ResponseBody ReadyResponse payReady2(OrderDto orderDto,HttpSession httpSession,Principal principal,
//                                                 CartOrderDto cartOrderDto) {
//        // 카카오 결제 준비하기	- 결제요청 service 실행
//        String email = memberService.loadMemberEmail(principal,httpSession);// principal 객체에서 현재 로그인한 회원의 이메일 정보를 조회합니다.
//        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
//        cartService.orderCartItem(cartOrderDtoList,email);
//        ReadyResponse readyResponse = kakaoPayService.payReady(itemFormDto,orderDto);
//
//        return readyResponse; // 클라이언트에 보냄.(tid,next_redirect_pc_url이 담겨있음.)
//
//    }

    // 결제승인요청
    @GetMapping(value = "/pay/completed")
    public String  approveResponse(Principal principal,HttpSession httpSession) {
        OrderDto orderDto = (OrderDto) httpSession.getAttribute("orderDto");
        String email = memberService.loadMemberEmail(principal,httpSession);// principal 객체에서 현재 로그인한 회원의 이메일 정보를 조회합니다.

        orderService.order(orderDto,email); // 화면으로 넘어오는 주문 정보와 회원의 이메일 정보를 이용하여 주문로직을 호출합니다.

        return "redirect:/";
    }

    // 결제 취소시 실행 url
    @GetMapping("/pay/cancel")
    public String payCancel() {
        System.out.println("취소");
        return "redirect:/";
    }

    // 결제 실패시 실행 url
    @GetMapping("/pay/fail")
    public String payFail() {
        System.out.println("실패");
        return "redirect:/";
    }

}
