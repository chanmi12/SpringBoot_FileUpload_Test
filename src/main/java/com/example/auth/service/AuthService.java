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

    public User getLoginUser(String uniqueId) {
        return userRepository
                .findByUniqueId(uniqueId)
                .orElseThrow(() -> new DoNotExistException("해당 유저가 없습니다."));
    }

    public AuthDto login(AuthDto dto) {
        Optional<User> user = userRepository.findByUniqueId(dto.getUniqueId());

        if (user.isEmpty()) {
            // Register a new user if they don't exist
            User newUser = User.from(dto);
            userRepository.save(newUser);

            return AuthDto.builder()
                    .token(JwtUtil.createToken(newUser.getUniqueId(), newUser.getName(), SECRET_KEY))
                    .build();

        } else {
            // Update the existing user if they are found
            user.get().update(dto);

            return AuthDto.builder()
                    .token(JwtUtil.createToken(user.get().getUniqueId(), user.get().getName(), SECRET_KEY))
                    .build();
        }
    }
}