package com.shop3.shop3.config;

import lombok.AllArgsConstructor;

import java.util.Map;
@AllArgsConstructor
public class NaverUserInfo implements Oauth2UserInfo {

    private Map<String, Object> attributes;

    // @Override
    // public String getProviderId() {
    //   return (String) attributes.get("id");
    // }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

}
