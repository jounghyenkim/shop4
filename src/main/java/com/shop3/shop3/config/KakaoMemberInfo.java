package com.shop3.shop3.config;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class KakaoMemberInfo implements Oauth2UserInfo {
    private Map<String, Object> attributes;

  /*  @Override
    public String getProviderId() {
        // Long 타입이기 때문에 toString으로 변호나
        return attributes.get("id").toString();
    }*/

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        // kakao_account라는 Map에서 추출
        return (String) ((Map) attributes.get("kakao_account")).get("email");
    }

    @Override
    public String getName() {
        // kakao_account라는 Map에서 추출
        return (String) ((Map) attributes.get("properties")).get("nickname");
    }
}
