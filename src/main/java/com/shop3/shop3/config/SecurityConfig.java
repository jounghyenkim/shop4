package com.shop3.shop3.config;

import com.shop3.shop3.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    MemberService memberService;
    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(new CookieCsrfTokenRepository())   // csrf 토큰 저장소를 http only cookie로 설정
                )
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(authorizeHttpRequestsCustomizer -> authorizeHttpRequestsCustomizer
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                        .requestMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                ).formLogin(formLoginCustomizer -> formLoginCustomizer
                        .loginPage("/members/login")
                        .defaultSuccessUrl("/", true)
                        .usernameParameter("email")
                        .failureUrl("/members/login/error")
                        .failureHandler(new FormLoginAuthenticationFailureHandler())
                ).logout( logoutCustomizer -> logoutCustomizer
                        .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                        .logoutSuccessUrl("/")
                ) .oauth2Login(oauthLogin -> oauthLogin
                        .defaultSuccessUrl("/")
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(principalOauth2UserService))
                ).exceptionHandling(e -> e
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )
                .build()
                ;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() { // 소셜 로그인 에러 대처
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/favicon.ico", "/resources/**", "/error");
    }
}
