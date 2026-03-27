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
}
