package com.example.sign;

import com.example.user.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class SignMapper {

    public static SignDto toDto(Sign sign) {
        SignDto signDto = new SignDto();
        sign.setId(sign.getId());
        signDto.setId(sign.getId());
        signDto.setPath(sign.getPath());
        signDto.setSaved(sign.isSaved());
        signDto.setDeleted(sign.isDeleted());
        signDto.setCreateDate(sign.getCreateDate());
        signDto.setUpdateDate(sign.getUpdateDate());

        return signDto;
    }

    public static Sign toEntity(SignDto signDto){
        Sign sign = new Sign();
        sign.setId(signDto.getId());
        sign.setPath(signDto.getPath());
        sign.setSaved(signDto.isSaved());
        sign.setDeleted(signDto.isDeleted());
        sign.setCreateDate(signDto.getCreateDate());
        sign.setUpdateDate(signDto.getUpdateDate());
        return sign;
    }
}
