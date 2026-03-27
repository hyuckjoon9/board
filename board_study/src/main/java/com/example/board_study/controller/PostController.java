package com.example.board_study.controller;

import com.example.board_study.controller.dto.post.PostCreateRequest;
import com.example.board_study.controller.dto.post.PostListResponse;
import com.example.board_study.controller.dto.post.PostResponse;
import com.example.board_study.controller.dto.post.PostUpdateRequest;
import com.example.board_study.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post", description = "게시글 관련 API")
@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService){
        this.postService=postService;
    }

    @Operation(summary = "게시글 목록 조회", description = "키워드 없으면 전체 페이징, 있으면 제목 기반 검색")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping
    public Page<PostListResponse> findPage(
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "검색 키워드(제목 기준, 없으면 전체 조회")
            @RequestParam(required = false) String keyword){
        PageRequest pageable = PageRequest.of(page, 10);
        if(keyword==null || keyword.isBlank()){
            return postService.findPage(pageable);
        }

        return postService.searchByTitle(keyword, pageable);
    }

    @Operation(summary = "게시글 단건 조회", description = "게시글 ID로 단건 조회, 댓글 목록 포함")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "리소스 없음")
    })
    @GetMapping("/{id}")
    public PostResponse findById(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id){
        return postService.findById(id);
    }

    @Operation(summary = "게시글 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/create")
    public void create(@RequestBody PostCreateRequest request){
        postService.create(request.getTitle(), request.getContent());
    }

    @Operation(summary = "게시글 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "리소스 없음")
    })
    @PutMapping("/{id}")
    public void update(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id,
            @RequestBody PostUpdateRequest request){
        postService.update(id, request.getTitle(), request.getContent());
    }

    @Operation(summary = "게시글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "리소스 없음")
    })
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id){
        postService.delete(id);
    }
}