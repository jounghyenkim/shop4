package com.shop3.shop3.dto;

import com.shop3.shop3.entity.Comment;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.ui.ModelMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter @Setter
public class CommentDto {
    private Long id;
    private Long boardId;
    private Long parentId;
    private String memberName;

    private String content;
    private LocalDateTime regTime;
    private String UpTime;

    private static ModelMapper modelMapper = new ModelMapper();
    public static CommentDto of(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setUpTime(comment.getRegTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        commentDto.setMemberName(comment.getMember().getName());
        if (comment.getParent() != null) {
            commentDto.setParentId(comment.getParent().getId());
        }
        return modelMapper.map(comment, CommentDto.class);
    }
}
