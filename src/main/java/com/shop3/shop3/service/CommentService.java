package com.shop3.shop3.service;


import com.shop3.shop3.dto.CommentDto;
import com.shop3.shop3.entity.Board;
import com.shop3.shop3.entity.Comment;
import com.shop3.shop3.entity.Member;
import com.shop3.shop3.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberService memberService;
    private final BoardService boardService;

    public List<CommentDto> mainCommentList(Long boardId){
        List<Comment> commentList = commentRepository.findByBoardIdAndParentNullOrderByIdAsc(boardId);
        List<CommentDto> mainCommentList = new ArrayList<>();

        for (Comment comment : commentList){
            CommentDto commentDto = CommentDto.of(comment);
            mainCommentList.add(commentDto);
        }
        return mainCommentList;
    }
    public List<CommentDto> subCommentList(Long boardId){
        List<Comment> commentList = commentRepository.findByBoardIdAndParentNotNullOrderByIdAsc(boardId);
        List<CommentDto> subCommentList = new ArrayList<>();
        for (Comment comment : commentList){
            CommentDto commentDto = CommentDto.of(comment);
            subCommentList.add(commentDto);
        }
        return subCommentList;
    }

    public Comment commentWrite(HttpSession httpSession, Principal principal, CommentDto commentDto){
        Member member = memberService.findMember(httpSession,principal);
        Board board = boardService.getId(commentDto.getBoardId());
        Comment parent =null;
        if (commentDto.getParentId() != null){
            parent = commentRepository.findById(commentDto.getParentId())
                    .orElseThrow(EntityNotFoundException::new);
        }
        Comment comment =new Comment(board,member,commentDto.getContent(),parent);
        return commentRepository.save(comment);
    }
    public Comment findComment(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(EntityNotFoundException::new);
    }
    public Long commentDelete(Comment comment){
        Long boardId = comment.getBoard().getId();
        commentRepository.delete(comment);
        return boardId;
    }
    public Long commentUpdate(Comment comment,String content){

        return comment.updateComment(content);
    }


}
