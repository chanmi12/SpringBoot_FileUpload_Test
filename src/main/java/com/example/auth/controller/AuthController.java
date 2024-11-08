package com.example.auth.controller;

import com.example.auth.controller.request.LoginRequest;
import com.example.auth.controller.response.LoginResponse;
import com.example.auth.dto.AuthDto;
import com.example.auth.service.AuthService;
import com.example.auth.service.HisnetLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthService authService;
    @Autowired
    private final HisnetLoginService hisnetLoginService;

@PostMapping("/login")
public ResponseEntity<Long> login(@RequestBody LoginRequest request) {
    // HisnetLoginService를 사용하여 사용자 정보를 가져옴
    AuthDto authDto = hisnetLoginService.callHisnetLoginApi(request.getToken());
    // AuthService를 사용하여 사용자 정보를 바탕으로 User를 찾거나 생성
    Long userId = authService.loginOrCreateUser(authDto);
    // User의 ID를 반환
    return ResponseEntity.ok(userId);
}
}
