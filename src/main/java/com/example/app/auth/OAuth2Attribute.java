package com.example.app.auth;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;

@ToString
@Builder(access = AccessLevel.PRIVATE)
@Getter
@Slf4j
public class OAuth2Attribute {
    private Map<String, Object> attributes;
    private String attributeKey;
    private String email;
    private String name;
    private String profile;
    private String provider;

    public static OAuth2Attribute of(String provider, String attributeKey, Map<String, Object> attributes){
        switch (provider){
            case "google":
                return ofGoogle(provider, attributeKey, attributes);
            case "naver":
                return ofNaver(provider, attributeKey, attributes);
            case "kakao":
                return ofKakao(provider, attributeKey, attributes);
            default:
                throw new RuntimeException();
        }
    }

//    Google
    private static OAuth2Attribute ofGoogle(String provider, String attributeKey, Map<String, Object> attributes){
        log.info(attributes.toString());
        return OAuth2Attribute.builder()
                .email((String)attributes.get("email"))
                .provider(provider)
                .attributes(attributes)
                .attributeKey(attributeKey)
                .build();
    }

//    Kakao
    private static OAuth2Attribute ofKakao(String provider, String attributeKey, Map<String, Object> attributes){
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        log.info(kakaoAccount.toString());
        log.info(kakaoProfile.toString());

        return OAuth2Attribute.builder()
                .email((String) kakaoAccount.get("email"))
                .provider(provider)
                .attributes(kakaoAccount)
                .attributeKey(attributeKey)
                .build();
    }

//    Naver
    private static OAuth2Attribute ofNaver(String provider, String attributeKey, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        log.info(response.toString());

        return OAuth2Attribute.builder()
                .email((String) response.get("email"))
                .attributes(response)
                .provider(provider)
                .attributeKey(attributeKey)
                .build();
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", attributeKey);
        map.put("key", attributeKey);
        map.put("email", email);
        map.put("provider", provider);

        return map;
    }
}
