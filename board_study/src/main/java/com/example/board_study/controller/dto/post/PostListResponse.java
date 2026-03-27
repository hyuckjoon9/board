package com.example.board_study.controller.dto.post;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostListResponse {
    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    @Schema(description = "게시글 제목", example = "제목입니다.")
    private String title;

    @Schema(description = "게시글 내용", example = "내용입니다.")
    private String content;
//    public PostListResponse(Long id, String title, String content){
//        this.id=id;
//        this.title=title;
//        this.content=content;
//    }


}
