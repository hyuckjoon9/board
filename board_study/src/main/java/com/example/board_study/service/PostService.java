package com.example.board_study.service;

import com.example.board_study.controller.dto.comment.CommentResponse;
import com.example.board_study.controller.dto.post.PostListResponse;
import com.example.board_study.controller.dto.post.PostResponse;
import com.example.board_study.domain.Comment;
import com.example.board_study.domain.Post;
import com.example.board_study.domain.User;
import com.example.board_study.exception.ForbiddenException;
import com.example.board_study.exception.NotFoundException;
import com.example.board_study.repository.CommentRepository;
import com.example.board_study.repository.PostRepository;
import com.example.board_study.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PostService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    PostService(PostRepository postRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    private Post findPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));
    }

    private User findUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
    }

    public List<PostListResponse> findAll() {
        return postRepository.findAll().stream()
                .map(post -> new PostListResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getAuthor().getUsername(),
                        post.getCreatedAt().format(FORMATTER)
                ))
                .toList();
    }

    public Page<PostListResponse> findPage(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(post -> new PostListResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getAuthor().getUsername(),
                        post.getCreatedAt().format(FORMATTER)
                ));
    }

    public Page<PostListResponse> searchByTitle(String keyword, Pageable pageable) {
        return postRepository.findByTitleContaining(keyword, pageable)
                .map(post -> new PostListResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getAuthor().getUsername(),
                        post.getCreatedAt().format(FORMATTER)
                ));
    }

    public PostResponse findById(Long id) {
        Post post = findPostOrThrow(id);
        List<Comment> comments = commentRepository.findByPostId(id);

        List<CommentResponse> commentResponses = comments.stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getAuthor().getUsername(),
                        comment.getCreatedAt().format(FORMATTER)
                ))
                .toList();

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getUsername(),
                post.getCreatedAt().format(FORMATTER),
                commentResponses
        );
    }

    public void create(String title, String content, String username) {
        User author = findUserOrThrow(username);
        Post post = new Post(title, content, author);
        postRepository.save(post);
    }

    @Transactional
    public void update(Long id, String title, String content, String username) {
        Post post = findPostOrThrow(id);
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new ForbiddenException("접근 권한이 없습니다.");
        }
        post.update(title, content);
    }

    @Transactional
    public void delete(Long id, String username) {
        Post post = findPostOrThrow(id);
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new ForbiddenException("접근 권한이 없습니다.");
        }
        postRepository.delete(post);
    }
}
