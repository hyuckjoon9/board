package com.example.board_study.controller;

import com.example.board_study.controller.dto.comment.CommentCreateRequest;
import com.example.board_study.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "댓글 관련 API")
@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/post/{postId}/comments")
public class CommentController {
    private final CommentService commentService;
    CommentController(CommentService commentService){
        this.commentService=commentService;
    }

    @Operation(summary = "댓글 작성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "리소스 없음")
    })
    @PostMapping
    public void writeComment(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId, @RequestBody CommentCreateRequest request){
        commentService.writeComment(postId, request);
    }

    @Operation(summary = "댓글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "리소스 없음")
    })
    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId,
            
            @Parameter(description = "댓글 ID", example = "1")
            @PathVariable Long commentId){
        commentService.deleteComment(postId, commentId);
    }
}
