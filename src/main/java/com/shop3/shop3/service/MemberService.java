package com.shop3.shop3.service;

import com.shop3.shop3.entity.Member;
import com.shop3.shop3.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 회원 저장
    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }
    // 회원 검증
    public void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다");
        }

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        if (member == null){
            throw new UsernameNotFoundException(email);
        }
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
    public String loadMemberEmail(Principal principal, HttpSession httpSession) { // 외부 로그인 멤버 와 회원가입 폼 Dto 로 들어온 멤버 분기
        if (principal != null) {
            if (httpSession.getAttribute("user") != null) {
                return (String) httpSession.getAttribute("email");
            } else {
                return principal.getName();
            }
        }
        return null;
    }
    public String loadMemberName(Principal principal, HttpSession httpSession) { // 외부 로그인 멤버 와 회원가입 폼 Dto 로 들어온 멤버 분기
        String userName = "";
        String MemberName = (String) httpSession.getAttribute("name");

        if (principal != null) {
            if (httpSession.getAttribute("user") != null) {
                return MemberName;
            } else {
                userName = principal.getName();
                Member member = memberRepository.findByEmail(userName);
                return member.getName();
            }
        }
        return null;
    }
}
