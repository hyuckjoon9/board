package com.example.board_study.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(
        name = "posts",
        indexes = {
                // 복합 인덱스: 특정 사용자의 게시글 목록을 최신순으로 조회할 때
                // WHERE user_id = ? ORDER BY created_at DESC 패턴을 커버링 인덱스로 처리.
                // user_id를 leading column으로 두어 등치(=) 필터링 후,
                // created_at 정렬을 인덱스 순서로 해결해 filesort 제거.
                @Index(name = "idx_posts_user_id_created_at", columnList = "user_id, created_at"),

                // 전체 목록 페이징 기본 정렬용 단일 인덱스.
                // ORDER BY created_at DESC LIMIT n 패턴에서 filesort 없이 인덱스 스캔으로 처리.
                @Index(name = "idx_posts_created_at", columnList = "created_at")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;
    String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
