package com.example.auth.controller;

import com.example.auth.controller.request.LoginRequest;
import com.example.auth.controller.response.LoginResponse;
import com.example.auth.dto.AuthDto;
import com.example.auth.service.AuthService;
import com.example.auth.service.HisnetLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final HisnetLoginService hisnetLoginService;
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        AuthDto authDto = hisnetLoginService.callHisnetLoginApi(new AuthDto(request.getHisnetToken()));
        return ResponseEntity.ok(LoginResponse.from(authService.login(authDto)));
    }

}
