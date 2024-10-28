package com.example.auth.service;

import com.example.auth.dto.AuthDto;
import com.example.auth.exception.DoNotExistException;
import com.example.auth.util.JwtUtil;
import com.example.user.User;
import com.example.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Value("${custom.jwt.secret}")
    private String SECRET_KEY;

    public User getLoginUser(Long uniqueId) {
        return userRepository
                .findById(uniqueId)
                .orElseThrow(() -> new DoNotExistException("해당 유저가 없습니다."));
    }

    public AuthDto login(AuthDto dto) {
        Optional<User> user = userRepository.findById(Long.valueOf(dto.getUniqueId()));
        if (user.isEmpty()) {
            User newUser = User.from(dto);
            userRepository.save(User.from(dto));
            return AuthDto.builder()
                    .token(JwtUtil.createToken(newUser.getUniqueId(), newUser.getName(), SECRET_KEY))
                    .build();
        } else {
            user.get().update(dto);
            return AuthDto.builder()
                    .token(
                            JwtUtil.createToken(
                                    user.get().getUniqueId(), user.get().getName(), SECRET_KEY))
                    .build();
        }
    }
}