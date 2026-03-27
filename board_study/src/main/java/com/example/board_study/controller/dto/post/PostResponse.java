package com.example.board_study.controller.dto.post;

import com.example.board_study.controller.dto.comment.CommentResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostResponse {
    @Schema(description = "게시글 ID", example = "1")
    private Long id;
    @Schema(description = "게시글 제목", example = "제목입니다.")
    private String title;
    @Schema(description = "게시글 내용", example = "내용입니다.")
    private String content;
    @Schema(description = "댓글 리스트")
    private List<CommentResponse> comments;
}
