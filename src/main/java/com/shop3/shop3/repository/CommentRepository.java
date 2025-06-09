package com.shop3.shop3.repository;

import com.shop3.shop3.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByBoardIdAndParentNullOrderByIdAsc(Long boardId);
    List<Comment> findByBoardIdAndParentNotNullOrderByIdAsc(Long boardId);

}
