package com.example.sign;


import com.example.user.UserDto;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private LocalDateTime createDate;
    private LocalDateTime updateDate;


}
