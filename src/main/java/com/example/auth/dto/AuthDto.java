package com.example.auth.dto;

import com.example.auth.controller.request.LoginRequest;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AuthDto {
    private String uniqueId;
    private String hisnetToken;
    private String token;
    private String name;
    private String email;
    private String department;
    private String major1;
    private String major2;
    private Integer grade;
    private Integer semester;

    public AuthDto(String hisnetToken) {
        this.hisnetToken = hisnetToken;
    }

    public static AuthDto from(LoginRequest request) {
        return AuthDto.builder().hisnetToken(request.getToken()).build();
    }
}
