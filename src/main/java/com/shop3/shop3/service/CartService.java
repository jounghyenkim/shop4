package com.shop3.shop3.service;

import com.shop3.shop3.dto.CartDetailDto;
import com.shop3.shop3.dto.CartItemDto;
import com.shop3.shop3.dto.CartOrderDto;
import com.shop3.shop3.dto.OrderDto;
import com.shop3.shop3.entity.Cart;
import com.shop3.shop3.entity.CartItem;
import com.shop3.shop3.entity.Item;
import com.shop3.shop3.entity.Member;
import com.shop3.shop3.repository.CartItemRepository;
import com.shop3.shop3.repository.CartRepository;
import com.shop3.shop3.repository.ItemRepository;
import com.shop3.shop3.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto,String email){
        Item item = itemRepository.findById(cartItemDto.getItemId()) // 장바구니에 담을 상품 엔티티를 조회합니다.
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email); // 현재 로그인한 회원 엔티티를 조회합니다.

        Cart cart = cartRepository.findByMemberId(member.getId()); // 현재 로그인한 회원의 장바구니 엔티티를 조회합니다.
        if (cart == null){ // 상품을 처음으로 장바구니에 담을 경우 해당 회원의 장바구니 엔티티를 생성합니다
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }
        CartItem savedCartItem = // 현재 상품이 장바구니에 이미 들어가있는지 조회합니다.
                cartItemRepository.findByCartIdAndItemId(cart.getId(),item.getId());

        if (savedCartItem != null){ // 장바구니에 이미 들어가있는 상품일경우 기존 수량에 현재 장바구니에 담을 수량 만큼 더해줍니다.
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();

        }else{
            CartItem cartItem = CartItem.createCartItem(cart,item,cartItemDto.getCount());// 장바구니,상품 엔티티, 장바구니에 담을 수량만큼 더해줍니다.
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }
    @Transactional
    public List<CartDetailDto> getCartList(String email){

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId()); // 현재 로그인한 회원의 장바구니 엔티티를 조회합니다.
        if (cart == null){ // 장바구니에 상품을 한번도 안담았을 경우 장바구니 엔티티가 없으므로 빈 리스트를 반환합니다.
            return cartDetailDtoList;
        }
        // 장바구니에 담겨있는 상품정보를 조회합니다.
        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());

        return cartDetailDtoList;
    }

    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId,String email){
        Member curMember = memberRepository.findByEmail(email);// 현재 로그인한 회원을 조회합니다.
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember(); // 장바구니 상품을 저장한 회원을 조회합니다.

        // 현재 로그인한 회원과 장바구니 상품을 저장한 회원이 다를경우 false, 같으면 true를 반환합니다.
        if (!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }
        return true;
    }
    // 장바구니 상품의 수량을 업데이트 하는 메소드입니다.
    public void updateCartItemCount(Long cartItemId,int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }
    public void deleteCartItem(Long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList =new ArrayList<>();
        for (CartOrderDto cartOrderDto : cartOrderDtoList){ // 장바구니 페이지에서 전달받은 주문 상품번호를 이용하여 주문로직으로 전달할 orderDto 객체를만듭니다.
            CartItem cartItem =cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto =new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }
        Long orderId = orderService.orders(orderDtoList,email); // 장바구니에 담은 상품을 주문하도록 주문로직을 호출합니다.

        for (CartOrderDto cartOrderDto: cartOrderDtoList){
            CartItem cartItem= cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem); // 주문한 상품을 장바구니에서 제거합니다.
        }
        return orderId;
    }


}
