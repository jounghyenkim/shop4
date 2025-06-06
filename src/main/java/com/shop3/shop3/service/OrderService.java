package com.shop3.shop3.service;

import com.shop3.shop3.dto.OrderDto;
import com.shop3.shop3.dto.OrderHistDto;
import com.shop3.shop3.dto.OrderItemDto;
import com.shop3.shop3.entity.*;
import com.shop3.shop3.repository.ItemImgRepository;
import com.shop3.shop3.repository.ItemRepository;
import com.shop3.shop3.repository.MemberRepository;
import com.shop3.shop3.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;
    private final MemberService memberService;

    public Long order(OrderDto orderDto, String email){
        Item item = itemRepository.findById(orderDto.getItemId()) // 주문할 상품을 조회합니다.
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email); // 현재 로그인한 회원의 이메일정보를 이용해서 회원정보를 조회합니다.

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item,orderDto.getCount());// 주문할 상품 엔티티와 주문 수량을 이용하여 주문상품 엔티티를 생성합니다.
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member,orderItemList);// 회원 정보와 주문할 상품 리스트 정보를 이용하여 주문 엔티티를 생성합니다.
        orderRepository.save(order); // 생성한 주문 엔티티를 저장합니다.

        return order.getId();
    }
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(Principal principal, HttpSession httpSession, Pageable pageable){
        String email = memberService.loadMemberEmail(principal,httpSession);
        List<Order> orders = orderRepository.findOrders(email, pageable);
        System.out.println("email: " + email);
        System.out.println("orders: "+orders);
        Long totalCount = orderRepository.countOrder(email);
        List<OrderHistDto> orderHistDtos = new ArrayList<>();
        // Order -> OrderHistDto
        // OrderItem -> OrderItemDto
        for(Order order : orders){
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for(OrderItem orderItem : orderItems){
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(),
                        "Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto);
        }
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId,String email){ // 현재 로그인한 사용자와 주문 데이터를 생성한 사용자가 같은지 검사합니다.
        Member curMember = memberRepository.findByEmail(email); // 같을때는 true 를 반환하고 같지않을경우 false 를 반환합니다.
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember= order.getMember();

        if (!StringUtils.equals(curMember.getEmail(),savedMember.getEmail())){
            return false;
        }
        return true;
    }

    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder(); // 주문취소 상태로 변경하면 변경감지 기능에 의해서 트랜잭션이 끝날때 update 쿼리가 실행됩니다.
    }
    public Long orders(List<OrderDto> orderDtoList , String email){
        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList){ // 주문할 상품 리스트를 만들어줍니다.
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item,orderDto.getCount());
            orderItemList.add(orderItem);
        }
        Order order = Order.createOrder(member,orderItemList); // 현재 로그인한 회원과 주문 상품 목록을 이용하여 주문엔티티를 만듭니다
        orderRepository.save(order); // 주문데이터를 저장합니다.

        return order.getId();
    }
}

