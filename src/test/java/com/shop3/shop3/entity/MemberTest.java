package com.shop3.shop3.entity;

import com.shop3.shop3.constant.ItemSellStatus;
import com.shop3.shop3.constant.Role;
import com.shop3.shop3.dto.MemberFormDto;
import com.shop3.shop3.repository.MemberRepository;
import com.shop3.shop3.service.MemberService;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;


    public Member createMember(){
        MemberFormDto memberFormDto =new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto,passwordEncoder);
    }
    @Test
    @DisplayName("회원가입 테스트")
    public void saveMember(){
        Member member = createMember();
        Member saveMember = memberService.saveMember(member);

        assertEquals(member.getEmail(),saveMember.getEmail());
        assertEquals(member.getName(),saveMember.getName());
        assertEquals(member.getAddress(),saveMember.getAddress());
        assertEquals(member.getPassword(),saveMember.getPassword());
        assertEquals(member.getRole(),saveMember.getRole());
    }

    @Test
    @DisplayName("중복 회원 가입 테스트")
    public void saveDuplicateMemeberTest(){
        Member member1 = createMember();
        Member member2 = createMember();
        memberService.saveMember(member1);

        Throwable e = assertThrows(IllegalStateException.class,() ->{
            memberService.saveMember(member2);});
        assertEquals("이미 가입된 회원입니다",e.getMessage());
    }
    @Test
    @DisplayName("Auditing 테스트")
    @WithMockUser(username = "gildong",roles = "USER") //스프링 시큐리티에서 제공하는 어노테이션으로 @WithMockUser에 지정한
                                                        // 사용자가 로그인한 상태라고 가정하고 테스트를 진행할수있습니다.
    public void  auditingTest(){
        Member newMember = new Member();
        memberRepository.save(newMember);

        em.flush();
        em.clear();

        Member member = memberRepository.findById(newMember.getId())
                .orElseThrow(EntityNotFoundException::new);

        System.out.println("register time : " + member.getRegTime());
        System.out.println("update time: " + member.getUpdateTime());
        System.out.println("create member: " + member.getCreatedBy());
        System.out.println("modify member: " + member.getModifiedBy());



    }
}