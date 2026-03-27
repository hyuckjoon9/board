package com.example.board_study.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name="posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    String content;

    protected Post(){}
    public Post(String title, String content){
        this.title=title;
        this.content=content;
    }

    public void update(String title, String content){
        this.title=title;
        this.content=content;
    }
}
