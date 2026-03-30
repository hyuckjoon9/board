package com.example.board_study.service;

import com.example.board_study.controller.dto.auth.LoginRequest;
import com.example.board_study.controller.dto.auth.LoginResponse;
import com.example.board_study.controller.dto.auth.SignupRequest;
import com.example.board_study.domain.User;
import com.example.board_study.exception.UnauthorizedException;
import com.example.board_study.repository.UserRepository;
import com.example.board_study.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void signup(SignupRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("인증이 필요합니다."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("인증이 필요합니다.");
        }
        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponse(token);
    }
}
