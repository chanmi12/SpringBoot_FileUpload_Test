package com.example.sign;


import com.example.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignDto {
    private Long id;
    private UserDto user;
    private String path;
    private boolean saved;
    private boolean deleted;
}
