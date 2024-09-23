package com.example.user;

public class UserMapper {
    // Map User entity to UserDto
    public static UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCreateDate(user.getCreateDate());
        dto.setUpdateDate(user.getUpdateDate());
        dto.setLevel(user.getLevel());
        return dto;
    }

    // Map UserDto to User entity
    public static User toEntity(UserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setLevel(dto.getLevel());
        return user;
    }
}
