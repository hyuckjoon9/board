package com.example.board_study.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String content;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    protected Comment(){}
    public Comment(Post post, String content){
        this.post=post;
        this.content=content;
    }

}
