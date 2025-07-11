package com.shop3.shop3.controller;

import com.shop3.shop3.dto.OrderDto;
import com.shop3.shop3.dto.OrderHistDto;
import com.shop3.shop3.service.MemberService;
import com.shop3.shop3.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    // 스프링에서 비동기 처리를 할때 @RequestBody 와 @ResponseBody 어노테이션을 사용합니다.
    // @RequestBody: HTTP 요청의 본문 body 에 담긴 내용을 자바 객체로 전달
    // @ResponseBody: 자바 객체를 Http 요청의 body 로 전달
    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto,
                                              BindingResult bindingResult, Principal principal,HttpSession httpSession){
        if (bindingResult.hasErrors()) { // 주문 정보를 받는 orderDto 객체에 데이터 바인딩시 에러가 있는지 검사합니다.
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError: fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(),
                    HttpStatus.BAD_REQUEST); // 에러 정보를 ResponseEntity 객체에 담아서 반환합니다.
        }
        String email = memberService.loadMemberEmail(principal,httpSession);// principal 객체에서 현재 로그인한 회원의 이메일 정보를 조회합니다.
        Long orderId;
        try {
            orderId = orderService.order(orderDto,email); // 화면으로 넘어오는 주문 정보와 회원의 이메일 정보를 이용하여 주문로직을 호출합니다.
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(orderId,HttpStatus.OK);// 결과값으로 생성된 주문번호와 요청이 성공했다는 HTTP응답 상태 코드를 반환합니다.
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model,HttpSession httpSession){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principal,httpSession, pageable);
        System.out.println("orderHist: "+orderHistDtoList);
        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage",5);
        return "order/orderHist";
    }
    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal,HttpSession httpSession){
        String email = memberService.loadMemberEmail(principal,httpSession);

        // 자바 스크립트에서 취소할 주문 번호는 조작이 가능하므로 다른 사람의
        // 주문을 취소하지 못하도록 취소 권한 검사를합니다.
        if(!orderService.validateOrder(orderId,email)){
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.",
                    HttpStatus.FORBIDDEN);
        }
        orderService.cancelOrder(orderId); // 주문 취소 로직을 호출합니다.
        return new ResponseEntity<Long>(orderId,HttpStatus.OK);
    }
}
