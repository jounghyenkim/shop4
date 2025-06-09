package com.shop3.shop3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Comment extends BaseTimeEntity{
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @JsonIgnore
    private Board board;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @Column(length = 300,nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
    orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Comment> commentList =new ArrayList<>();
    public Comment(){};
    public Comment(Board board, Member member, String content, Comment parent) {
        this.board = board;
        this.member = member;
        this.content = content;
        this.parent = parent;
    }

    public Long updateComment(String content){
        this.content = content;
        return this.board.getId();
    }
}
