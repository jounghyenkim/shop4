package com.shop3.shop3.entity;

import com.shop3.shop3.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate; // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문상태

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, // 부모 엔티티의 영속성 상태변화를 자식엔티티에 모두 전이하는
                                                                // CascadeTypeAll 옵션을 설정합니다.
    orphanRemoval = true,fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime regTime;

    private LocalDateTime updateTime;

    // OrderItems 에는 주문 상품 정보들을 담아줍니다. orderItem 객체를 order 객체의 orderItems 에 추가합니다
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this); // Order 엔티티와 OrderItem 엔티티가 양방향 참조 관계이므로 , OrderItem 객체도 order 객체를 세팅합니다.
    }
    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member); // 상품을 주문한 회원의 정보를 세팅합니다.
        for (OrderItem orderItem : orderItemList){ // 상품 페이지에서는 1개의 상품을 주문하지만, 장바구니 페이지에서는 한번에 여러개의 상품을 주문
            order.addOrderItem(orderItem); // 할수 있습니다. 따라서 여러개의 주문 상품을 담을수 있도록 리스트 형태로 파라미터 값을 받으며 주문객체에
        }                                   // orderItem 객체를 추가합니다.
        order.setOrderStatus(OrderStatus.ORDER); // 주문 상태를 ORDER로 세팅합니다.
        order.setOrderDate(LocalDateTime.now());// 현재 시간을 주문시간으로 세팅합니다.
        return order;
    }
    public int getTotalPrice(){ // 총 주문 금액을 구하는 메소드입니다.
        int totalPrice = 0;
        for (OrderItem orderItem :orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;
        for (OrderItem orderItem : orderItems){
            orderItem.cancel();;
        }
    }
}
