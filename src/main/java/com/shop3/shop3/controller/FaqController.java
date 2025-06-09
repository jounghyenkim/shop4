package com.shop3.shop3.controller;

import com.shop3.shop3.dto.CommentDto;
import com.shop3.shop3.dto.WriteFormDto;
import com.shop3.shop3.entity.Board;
import com.shop3.shop3.repository.BoardRepository;
import com.shop3.shop3.service.BoardService;
import com.shop3.shop3.service.CommentService;
import com.shop3.shop3.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping(value = "/faq")
@RequiredArgsConstructor
public class FaqController {
    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final MemberService memberService;
    private final HttpSession httpSession;
    private final CommentService commentService;

    @GetMapping(value = "/boardLists")
    public String boardList(Model model, @PageableDefault(direction = Sort.Direction.DESC)Pageable pageable, Principal principal){
        Page<Board> boardList = boardService.getBoardList(pageable);
        model.addAttribute("boardList",boardList);
        String name = memberService.loadMemberName(principal,httpSession);
        model.addAttribute("name",name);
        model.addAttribute("maxPage",5);
        return "/faq/board";
    }

    @GetMapping(value = "/write")
    public String writeForm(Model model, WriteFormDto writeFormDto,Principal principal){
        String name = memberService.loadMemberName(principal,httpSession);
        model.addAttribute("name",name);
        model.addAttribute("writeFormDto",writeFormDto);

        return "/faq/writeBoardForm";
    }
    @PostMapping(value = "/write")
    public String write(@Valid WriteFormDto writeFormDto, BindingResult bindingResult,
                        Principal principal,Model model){
        if (bindingResult.hasErrors()){
            String name =memberService.loadMemberName(principal,httpSession);
            model.addAttribute("name",name);
            return "/faq/writeBoardForm";
        }
        Board board = boardService.wrtieBoard(writeFormDto,principal,httpSession);
        boardRepository.save(board);
        return "redirect:/faq/boardLists";
    }

    @GetMapping(value = "/detail/{id}")
    public String userBoardDetail(Model model, @PathVariable("id") Long boardId, Principal principal) {
        try {
            String name = memberService.loadMemberName(principal, httpSession);
            WriteFormDto writeFormDto = WriteFormDto.of(boardService.getId(boardId));

            List<CommentDto> mainCommentList = commentService.mainCommentList(boardId);
            List<CommentDto> subCommentList = commentService.subCommentList(boardId);

            model.addAttribute("name", name);
            model.addAttribute("writeFormDto", writeFormDto);

            model.addAttribute("mainCommentList", mainCommentList);
            model.addAttribute("subCommentList", subCommentList);
        } catch (Exception e) {
            System.out.println("~~~~~~~~~~~~~~상세보기 에러~~~~~~~~~~~~~~~~~~");
            return "redirect:/faq/boardLists";
        }


        return "/faq/userBoardDetail";
    }

    @GetMapping(value = "/boardDetail/{id}")
    public String boardDetail(Model model,@PathVariable("id") Long boardId,Principal principal){
        WriteFormDto writeFormDto = WriteFormDto.of(boardService.getId(boardId));
        String name =memberService.loadMemberName(principal,httpSession);
        model.addAttribute("writeFormDto",writeFormDto);
        model.addAttribute("name",name);
        return "/faq/boardDetail";
    }

    @PostMapping(value = "/update/{id}")
    public String update(@Valid WriteFormDto writeFormDto, BindingResult bindingResult,@PathVariable("id")Long boardId,
                         Model model , Principal principal){
        String name = memberService.loadMemberName(principal,httpSession);
        model.addAttribute("name",name);

        if (bindingResult.hasErrors()){
            return "/faq/boardDetail";
        }
        boardService.BoardAs(writeFormDto,boardId);
        return "redirect:/faq/boardLists";
    }
    @PostMapping(value = "/delete/{id}")
    public String delete(@PathVariable("id")Long boardId, Model model){
        boardService.deleteById(boardId);
        model.addAttribute("id",boardId);

        return "redirect:/faq/boardLists";
    }

}
