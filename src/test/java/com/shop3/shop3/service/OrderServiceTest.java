package com.shop3.shop3.service;

import com.shop3.shop3.constant.ItemSellStatus;
import com.shop3.shop3.dto.OrderDto;
import com.shop3.shop3.entity.Item;
import com.shop3.shop3.entity.Member;
import com.shop3.shop3.entity.Order;
import com.shop3.shop3.entity.OrderItem;
import com.shop3.shop3.repository.ItemRepository;
import com.shop3.shop3.repository.MemberRepository;
import com.shop3.shop3.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")

class OrderServiceTest {
     @Autowired
     OrderService orderService;

     @Autowired
     OrderRepository orderRepository;

     @Autowired
     ItemRepository itemRepository;

     @Autowired
     MemberRepository memberRepository;

    public Item saveItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }
    public Member saveMember(){
        Member member =new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);
    }
    @Test
    @DisplayName("주문 테스트")
    public void order(){
        Item item = saveItem();
        Member member =saveMember();

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);      // 주문할 상품과 상품 수량을 orderDto 객체에 세팅합니다.
        orderDto.setItemId(item.getId());

        Long orderId = orderService.order(orderDto,member.getEmail()); // 주문 로직 호출 결과 생성된 주문번호를 orderId 변수에 저장합니다.

        // 주문 번호를 이용하여 저장된 주문정보를 조회합니다.
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        List<OrderItem> orderItems = order.getOrderItems();

        // 주문한 상품의 총 가격을 구합니다.
        int totalPrice = orderDto.getCount() * item.getPrice();

        // 주문한 상품의 총 가격과 데이터베이스에 저장된 상품의 가격을 비교하여 같으면 테스트가 성공적으로 종료됩니다.
        assertEquals(totalPrice,order.getTotalPrice());

    }

}