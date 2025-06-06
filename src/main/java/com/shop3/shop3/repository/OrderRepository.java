package com.shop3.shop3.repository;

import com.shop3.shop3.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {


    // 현재 로그인한 사용자의 주문 데이터를 페이징 조건에 맞춰서 조회합니다.
    @Query("select o from Order o where o.member.email = :email order by o.orderDate desc")
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    // 현재 로그인한 회원의 주문 개수가 몇개인지 조회합니다.
    @Query("select count(o) from Order o where o.member.email = :email")
    Long countOrder(@Param("email") String email);
}
