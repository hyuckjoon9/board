package com.example.board_study.controller.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CommentCreateRequest {
    @Schema(description = "댓글 내용", example = "댓글 내용입니다.")
    private String content;
}
