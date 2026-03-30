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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private User createUser(String username) {
        User user = User.builder().username(username).password("encodedPassword").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private Post createPost(String title, String content, User author) {
        return Post.builder()
                .title(title)
                .content(content)
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Comment createComment(Post post, String content, User author) {
        return Comment.builder()
                .content(content)
                .post(post)
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("댓글 작성 - 성공")
    void writeComment_success() {
        // given
        User user = createUser("user1");
        Post post = createPost("제목", "내용", user);
        ReflectionTestUtils.setField(post, "id", 1L);
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", "댓글내용");
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(userRepository.findByUsername("user1")).willReturn(Optional.of(user));

        // when
        commentService.writeComment(1L, request, "user1");

        // then
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 작성 - 존재하지 않는 게시글 ID로 작성 시 NotFoundException 발생")
    void writeComment_postNotFound() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", "댓글내용");
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> commentService.writeComment(999L, request, "user1"));
    }

    @Test
    @DisplayName("댓글 삭제 - 성공")
    void deleteComment_success() {
        // given
        User user = createUser("user1");
        Post post = createPost("제목", "내용", user);
        ReflectionTestUtils.setField(post, "id", 1L);
        Comment comment = createComment(post, "댓글내용", user);
        ReflectionTestUtils.setField(comment, "id", 1L);
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        // when
        commentService.deleteComment(1L, 1L, "user1");

        // then
        verify(commentRepository).delete(comment);
    }

    @Test
    @DisplayName("댓글 삭제 - 존재하지 않는 댓글 ID로 삭제 시 NotFoundException 발생")
    void deleteComment_commentNotFound() {
        // given
        given(commentRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> commentService.deleteComment(1L, 999L, "user1"));
    }

    @Test
    @DisplayName("댓글 삭제 - 다른 게시글의 댓글 삭제 시 IllegalArgumentException 발생")
    void deleteComment_mismatchedPost() {
        // given
        User user = createUser("user1");
        Post post = createPost("제목", "내용", user);
        ReflectionTestUtils.setField(post, "id", 1L);
        Comment comment = createComment(post, "댓글내용", user);
        ReflectionTestUtils.setField(comment, "id", 1L);
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        // when & then - 댓글은 게시글 1번에 속하지만, 게시글 2번으로 요청
        assertThrows(IllegalArgumentException.class, () -> commentService.deleteComment(2L, 1L, "user1"));
    }

    @Test
    @DisplayName("댓글 삭제 - 작성자가 다를 때 ForbiddenException 발생")
    void deleteComment_forbidden() {
        // given
        User owner = createUser("owner");
        Post post = createPost("제목", "내용", owner);
        ReflectionTestUtils.setField(post, "id", 1L);
        Comment comment = createComment(post, "댓글내용", owner);
        ReflectionTestUtils.setField(comment, "id", 1L);
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        // when & then
        assertThrows(ForbiddenException.class, () -> commentService.deleteComment(1L, 1L, "other"));
    }
}
