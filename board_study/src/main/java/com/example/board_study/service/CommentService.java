package com.example.board_study.service;

import com.example.board_study.controller.dto.comment.CommentCreateRequest;
import com.example.board_study.domain.Comment;
import com.example.board_study.domain.Post;
import com.example.board_study.domain.User;
import com.example.board_study.exception.ForbiddenException;
import com.example.board_study.exception.NotFoundException;
import com.example.board_study.repository.CommentRepository;
import com.example.board_study.repository.PostRepository;
import com.example.board_study.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void writeComment(Long postId, CommentCreateRequest request, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        Comment comment = new Comment(post, request.getContent(), author);
        commentRepository.save(comment);
    }

    public void deleteComment(Long postId, Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("게시글에 속한 댓글이 아닙니다.");
        }

        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new ForbiddenException("접근 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}
