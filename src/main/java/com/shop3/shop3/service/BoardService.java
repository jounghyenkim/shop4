package com.shop3.shop3.service;

import com.shop3.shop3.dto.WriteFormDto;
import com.shop3.shop3.entity.Board;
import com.shop3.shop3.entity.Member;
import com.shop3.shop3.repository.BoardRepository;
import com.shop3.shop3.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    private final MemberRepository memberRepository;

    private final MemberService memberService;

    public Board wrtieBoard(WriteFormDto writeFormDto, Principal principal, HttpSession httpSession){
        String email = memberService.loadMemberEmail(principal,httpSession);

        Member member = memberRepository.findByEmail(email);

        Member member1 = memberRepository.findById(member.getId())
                .orElseThrow(EntityNotFoundException::new);
        Board board = Board.writeBoard(writeFormDto,member1,memberService,principal,httpSession);

        boardRepository.save(board);
        return board;
    }
    public List<Board> getList(){
        return this.boardRepository.findAll();
    }
    public Board getId(Long boardId){
        return boardRepository.findById(boardId)
                .orElseThrow(EntityNotFoundException::new);
    }
    public void BoardAs(WriteFormDto writeFormDto,Long id){
        Board board =boardRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        board = board.boardAs(writeFormDto);
        boardRepository.save(board);
    }
    public void deleteById(Long id){
        boardRepository.delete(
                boardRepository.findById(id).orElseThrow(EntityNotFoundException::new)
        );
    }
    public Page<Board> getBoardList(Pageable pageable){
        int page =(pageable.getPageNumber()) == 0 ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.DESC,"id"));
        return boardRepository.findAll(pageable);
    }
}
