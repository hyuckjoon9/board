package com.example.board_study.repository;

import com.example.board_study.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<Post> searchByTitle(String keyword, Pageable pageable);
}
