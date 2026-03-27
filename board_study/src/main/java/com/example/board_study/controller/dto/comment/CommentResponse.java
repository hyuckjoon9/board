package com.example.board_study.controller.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponse {
    @Schema(description = "댓글 ID", example = "1")
    private Long id;
    @Schema(description = "댓글 내용", example = "댓글 내용입니다.")
    private String content;
    @Schema(description = "작성자", example = "user1")
    private String username;
    @Schema(description = "작성시간", example = "2024-01-01 12:00")
    private String createdAt;
}
