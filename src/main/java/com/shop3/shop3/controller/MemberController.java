package com.shop3.shop3.controller;

import com.shop3.shop3.dto.MemberFormDto;
import com.shop3.shop3.entity.Member;
import com.shop3.shop3.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto",new MemberFormDto());

        return "member/memberForm";
    }
    @PostMapping(value = "/new")
    public String newMember(@Valid  MemberFormDto memberFormDto, BindingResult bindingResult
    ,Model model){
        if (bindingResult.hasErrors()){
            return "member/memberForm";
        }
        try{
            Member member = Member.createMember(memberFormDto,passwordEncoder);
            memberService.saveMember(member);
        }catch (IllegalStateException e){
            model.addAttribute("errorMessage",e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/";
    }
    @GetMapping(value = "/login")
    public String login(){
        return "member/memberLoginForm";
    }
    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg","아이디 또는 비밀번호를 확인해주세요");
        return "/member/memberLoginForm";
    }



}
