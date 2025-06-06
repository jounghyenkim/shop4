package com.shop3.shop3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "cart")
@ToString
public class Cart extends BaseEntity{

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);

        return cart;
    }
}
