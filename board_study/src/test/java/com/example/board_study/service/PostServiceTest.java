package com.example.board_study.service;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

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

    @Test
    @DisplayName("게시글 전체 조회 - 성공")
    void findAll_success() {
        // given
        User user = createUser("user1");
        Post post = createPost("제목", "내용", user);
        ReflectionTestUtils.setField(post, "id", 1L);
        given(postRepository.findAll()).willReturn(List.of(post));

        // when
        List<PostListResponse> result = postService.findAll();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("제목");
        assertThat(result.get(0).getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("게시글 페이지 조회 - 성공")
    void findPage_success() {
        // given
        User user = createUser("user1");
        Post post = createPost("제목", "내용", user);
        ReflectionTestUtils.setField(post, "id", 1L);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> page = new PageImpl<>(List.of(post), pageable, 1);
        given(postRepository.findAll(pageable)).willReturn(page);

        // when
        Page<PostListResponse> result = postService.findPage(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("제목");
    }

    @Test
    @DisplayName("게시글 제목 검색 - 성공")
    void searchByTitle_success() {
        // given
        User user = createUser("user1");
        Post post = createPost("검색제목", "내용", user);
        ReflectionTestUtils.setField(post, "id", 1L);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> page = new PageImpl<>(List.of(post), pageable, 1);
        given(postRepository.findByTitleContaining("검색", pageable)).willReturn(page);

        // when
        Page<PostListResponse> result = postService.searchByTitle("검색", pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("검색제목");
    }

    @Test
    @DisplayName("게시글 단건 조회 - 성공")
    void findById_success() {
        // given
        User user = createUser("user1");
        Post post = createPost("제목", "내용", user);
        ReflectionTestUtils.setField(post, "id", 1L);
        Comment comment = Comment.builder()
                .content("댓글내용")
                .post(post)
                .author(user)
                .createdAt(LocalDateTime.now())
                .build();
        ReflectionTestUtils.setField(comment, "id", 1L);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(commentRepository.findByPostId(1L)).willReturn(List.of(comment));

        // when
        PostResponse result = postService.findById(1L);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getContent()).isEqualTo("내용");
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().get(0).getContent()).isEqualTo("댓글내용");
    }

    @Test
    @DisplayName("게시글 단건 조회 - 존재하지 않는 ID 조회 시 NotFoundException 발생")
    void findById_notFound() {
        // given
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> postService.findById(999L));
    }

    @Test
    @DisplayName("게시글 생성 - 성공")
    void create_success() {
        // given
        User user = createUser("user1");
        given(userRepository.findByUsername("user1")).willReturn(Optional.of(user));

        // when
        postService.create("제목", "내용", "user1");

        // then
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 수정 - 성공")
    void update_success() {
        // given
        User user = createUser("user1");
        Post post = createPost("제목", "내용", user);
        ReflectionTestUtils.setField(post, "id", 1L);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // when
        postService.update(1L, "수정된 제목", "수정된 내용", "user1");

        // then
        assertThat(post.getTitle()).isEqualTo("수정된 제목");
        assertThat(post.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("게시글 수정 - 존재하지 않는 ID 수정 시 NotFoundException 발생")
    void update_notFound() {
        // given
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> postService.update(999L, "제목", "내용", "user1"));
    }

    @Test
    @DisplayName("게시글 수정 - 작성자가 다를 때 ForbiddenException 발생")
    void update_forbidden() {
        // given
        User owner = createUser("owner");
        Post post = createPost("제목", "내용", owner);
        ReflectionTestUtils.setField(post, "id", 1L);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // when & then
        assertThrows(ForbiddenException.class, () -> postService.update(1L, "제목", "내용", "other"));
    }

    @Test
    @DisplayName("게시글 삭제 - 성공")
    void delete_success() {
        // given
        User user = createUser("user1");
        Post post = createPost("제목", "내용", user);
        ReflectionTestUtils.setField(post, "id", 1L);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // when
        postService.delete(1L, "user1");

        // then
        verify(postRepository).delete(post);
    }

    @Test
    @DisplayName("게시글 삭제 - 존재하지 않는 ID 삭제 시 NotFoundException 발생")
    void delete_notFound() {
        // given
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> postService.delete(999L, "user1"));
    }

    @Test
    @DisplayName("게시글 삭제 - 작성자가 다를 때 ForbiddenException 발생")
    void delete_forbidden() {
        // given
        User owner = createUser("owner");
        Post post = createPost("제목", "내용", owner);
        ReflectionTestUtils.setField(post, "id", 1L);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // when & then
        assertThrows(ForbiddenException.class, () -> postService.delete(1L, "other"));
    }
}
