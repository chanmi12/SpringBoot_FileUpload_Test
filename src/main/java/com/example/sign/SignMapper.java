package com.example.sign;

import com.example.user.UserMapper;

public class SignMapper {

    public static SignDto toDto(Sign sign){
        return new SignDto(sign.getId(), UserMapper.toDto(sign.getUser()), sign.getPath(), sign.isSaved(), sign.isDeleted());
    }
    public static Sign toEntity(SignDto signDto){
        Sign sign = new Sign();
        sign.setId(signDto.getId());
        sign.setId(signDto.getId());
        sign.setPath(signDto.getPath());
        sign.setSaved(signDto.isSaved());
        sign.setDeleted(signDto.isDeleted());
        return sign;
    }
}
