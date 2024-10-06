package com.example.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    //모든 사용자 정보 조회
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    //사용자 정보 조회
    public Optional<UserDto> getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toDto);
    }

    //사용자 정보 생성
    public UserDto createUser(UserDto userDto) {
        // Validate email
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }

        // Convert DTO to Entity
        User user = UserMapper.toEntity(userDto);

        // Set timestamps
        user.setCreateDate(LocalDateTime.now());
        user.setUpdateDate(LocalDateTime.now());

        // Save User and Convert Back to DTO
        return UserMapper.toDto(userRepository.save(user));
    }


    //사용자 정보 수정
    public UserDto updateUser(Long userId, UserDto userDto) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    existingUser.setName(userDto.getName());
                    existingUser.setEmail(userDto.getEmail());
                    existingUser.setLevel(userDto.getLevel());
                    existingUser.setUpdateDate(LocalDateTime.now());
                    return UserMapper.toDto(userRepository.save(existingUser));
                })
                .orElseThrow(() -> new RuntimeException("사용자 정보가 존재하지 않습니다."));
    }

    //사용자 정보 삭제
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public User findOrCreateUser(UserDto userDto) {
        return userRepository.findByEmail(userDto.getEmail()).orElseGet(() -> {
            User newUser = new User();
            newUser.setName(userDto.getName());
            newUser.setEmail(userDto.getEmail());
            return userRepository.save(newUser);
        });
    }
}
