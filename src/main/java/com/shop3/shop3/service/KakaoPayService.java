package com.shop3.shop3.service;

import com.shop3.shop3.dto.CountDto;
import com.shop3.shop3.dto.ItemFormDto;
import com.shop3.shop3.dto.OrderDto;
import com.shop3.shop3.dto.ReadyResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
@Transactional
public class KakaoPayService {
    private final OrderService orderService;
    private final MemberService memberService;

    public ReadyResponse payReady(ItemFormDto itemFormDto, OrderDto orderDto) {
        // 카카오가 요구한 결제요청request값을 담아줍니다.
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("cid", "TC0ONETIME");
        parameters.add("partner_order_id", "아이템");
        parameters.add("partner_user_id", "inflearn");
        parameters.add("item_name", itemFormDto.getItemNm());
        parameters.add("quantity", "1");
        parameters.add("total_amount", String.valueOf(itemFormDto.getPrice() * orderDto.getCount()));
        parameters.add("tax_free_amount", "0");
        parameters.add("approval_url", "http://localhost/kakao/pay/completed"); // 결제승인시 넘어갈 url
        parameters.add("cancel_url", "http://localhost/kakao/pay/cancel"); // 결제취소시 넘어갈 url
        parameters.add("fail_url", "http://localhost/kakao/pay/fail"); // 결제 실패시 넘어갈 url
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());
        // 외부url요청 통로 열기.
        RestTemplate template = new RestTemplate();
        String url = "https://kapi.kakao.com/v1/payment/ready";
        // template으로 값을 보내고 받아온 ReadyResponse값 readyResponse에 저장.
        ReadyResponse readyResponse = template.postForObject(url, requestEntity, ReadyResponse.class);
        // 받아온 값 return
        return readyResponse;
    }

    // header() 셋팅
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK 9ce58ff414debda5b9f89416f630410d"); // 어드민 키
        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        return headers;
    }

}
