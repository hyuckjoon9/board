package com.example.board_study.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    private LocalDateTime createdAt;

    protected Comment() {}

    public Comment(Post post, String content, User author) {
        this.post = post;
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }
}
