package com.shop3.shop3.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop3.shop3.constant.ItemSellStatus;
import com.shop3.shop3.entity.Item;
import com.shop3.shop3.entity.QItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createITemTest(){
        for (int i=0; i<=10; i++){
            Item item =new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);

        }
    }
    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNm(){
        this.createITemTest();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        for (Item item : itemList){
            System.out.println(item.toString());
        }

    }
    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest(){
        this.createITemTest();
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1","테스트 상품 상세 설명5");
        for (Item item : itemList){
            System.out.println(item.toString());
        }


    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest(){
        this.createITemTest();
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for (Item item: itemList){
            System.out.println(item.toString());
        }

    }
    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc(){
        this.createITemTest();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for (Item item : itemList){
            System.out.println(item.toString());
        }
    }
    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        this.createITemTest();
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for (Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
    public void findByItemDetailByNative(){
        this.createITemTest();
        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for (Item item : itemList){
            System.out.println(item.toString());
        }

    }
    @Test
    @DisplayName("Querydsl 조회테스트1")
    public void queryDslTest(){
        this.createITemTest();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());
        List<Item> itemList = query.fetch();

        for (Item item : itemList){
            System.out.println(item.toString());
        }
    }

    public void createItemTest2(){
        for (int i=0; i<=5; i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
        for (int i=6; i<=10; i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }
    @Test
    @DisplayName("상품 querydsl 조회 테스트2")
    public void querydslTest2(){

        this.createItemTest2();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QItem item = QItem.item;
        String itemDetail = "테스트 상품 상세 설명";
        int price= 10003;
        String itemSellStat= "SELL";

        booleanBuilder.and(item.itemDetail.like("%"+itemDetail + "%"));
        booleanBuilder.and(item.price.gt(price));

        if (StringUtils.equals(itemSellStat,ItemSellStatus.SELL)){
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable =PageRequest.of(0,5);
        Page<Item> itemPageResult= itemRepository.findAll(booleanBuilder,pageable);
        System.out.println("total elements : " + itemPageResult.getTotalElements());

        List<Item> resultITemList = itemPageResult.getContent();
        for (Item resultItem: resultITemList){
            System.out.println(resultItem.toString());
        }

    }
}