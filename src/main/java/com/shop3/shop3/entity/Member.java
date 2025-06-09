package com.shop3.shop3.entity;

import com.shop3.shop3.config.Oauth2UserInfo;
import com.shop3.shop3.config.PrincipalOauth2UserService;
import com.shop3.shop3.constant.Role;
import com.shop3.shop3.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity{
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;
    private String provider;
    private String tel;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Board> boardList = new ArrayList<>();
    public Member() {

    }


    public static Member createMember(MemberFormDto memberFormDto,
                                      PasswordEncoder passwordEncoder){
        Member member =new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setRole(Role.USER);
        return member;
    }

    public static Member userMember(Oauth2UserInfo oauth2UserInfo){
        Member member = new Member();
        member.setName(oauth2UserInfo.getName());
        member.setEmail(oauth2UserInfo.getEmail());
        member.setProvider(oauth2UserInfo.getProvider());
        member.setRole(Role.USER);
        return member;
    }

    public Member( Role role, Oauth2UserInfo oAuth2UserInfo, PrincipalOauth2UserService principalOauth2UserService) {
        this.name = oAuth2UserInfo.getName();
        this.email = principalOauth2UserService.saveEmail(oAuth2UserInfo);
        this.role = role;
        this.provider = oAuth2UserInfo.getProvider();
    }
}
