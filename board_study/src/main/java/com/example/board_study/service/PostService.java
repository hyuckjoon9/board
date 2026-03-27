package com.example.board_study.service;

import com.example.board_study.controller.dto.comment.CommentResponse;
import com.example.board_study.controller.dto.post.PostListResponse;
import com.example.board_study.controller.dto.post.PostResponse;
import com.example.board_study.domain.Comment;
import com.example.board_study.domain.Post;
import com.example.board_study.exception.NotFoundException;
import com.example.board_study.repository.CommentRepository;
import com.example.board_study.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    PostService(PostRepository postRepository, CommentRepository commentRepository)  {
        this.postRepository=postRepository;
        this.commentRepository=commentRepository;
    }

    private Post findPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));
    }

    public List<PostListResponse> findAll(){
        return postRepository.findAll().stream()
                .map(post-> new PostListResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent()
                ))
                .toList();
    }

    public Page<PostListResponse> findPage(Pageable pageable){
        return postRepository.findAll(pageable)
                .map(post->new PostListResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent()
                ));
    }

    public Page<PostListResponse> searchByTitle(String keyword, Pageable pageable){
        return postRepository.findByTitleContaining(
                keyword, pageable
        ).map(post->new PostListResponse(
                post.getId(),
                post.getTitle(),
                post.getContent()
        ));
    }

    public PostResponse findById(Long id){
        Post post = findPostOrThrow(id);
        List<Comment> comments = commentRepository.findByPostId(id);

        List<CommentResponse> commentResponses = comments.stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getContent()
                ))
                .toList();

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                commentResponses
        );
    }

    public void create(String title, String content){
        Post post = new Post(title, content);
        postRepository.save(post);
    }

    @Transactional
    public void update(Long id, String title, String content){
        Post post = findPostOrThrow(id);
        post.update(title, content);
    }

    @Transactional
    public void delete(Long id){
        Post post = findPostOrThrow(id);
        postRepository.delete(post);
    }
}
