package com.example.board_study.controller.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class LoginRequest {
    @Schema(description = "사용자 이름", example = "user1")
    private String username;

    @Schema(description = "비밀번호", example = "password123")
    private String password;
}
