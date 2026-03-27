package com.example.board_study.controller.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostUpdateRequest {
    @Schema(description = "게시글 제목", example = "제목입니다.")
    private String title;
    @Schema(description = "게시글 내용", example = "내용입니다.")
    private String content;
}
