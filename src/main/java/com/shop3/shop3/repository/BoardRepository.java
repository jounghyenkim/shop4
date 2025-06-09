package com.shop3.shop3.repository;

import com.shop3.shop3.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Long> {

    @Query(value = "SELECT * FROM board",nativeQuery = true)
    List<Board> findAll();
}
