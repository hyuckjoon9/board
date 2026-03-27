package com.example.board_study.controller.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostCreateRequest {
    @Schema(description = "게시글 제목", example = "제목이 들어있습니다.")
    private String title;
    @Schema(description = "게시글 내용", example = "내용이 들어있습니다.")
    private String content;
}
