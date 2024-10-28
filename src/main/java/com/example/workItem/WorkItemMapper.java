package com.example.workItem;


import com.example.sign.Sign;
import com.example.sign.SignDto;
import com.example.user.User;
import com.example.work.Work;
import org.springframework.stereotype.Component;



@Component
public class WorkItemMapper {

    public WorkItemDto toDto(WorkItem workItem) {
        WorkItemDto dto = new WorkItemDto();
        dto.setId(workItem.getId());
        dto.setWorkId(workItem.getWork().getId());
        dto.setUserId(workItem.getUser().getId());
        dto.setType(workItem.getType());
        dto.setText(workItem.getText());
        dto.setXPosition(workItem.getXPosition());
        dto.setYPosition(workItem.getYPosition());
        dto.setWidth(workItem.getWidth());
        dto.setHeight(workItem.getHeight());
        dto.setFree(workItem.getFree());
        dto.setPage(workItem.getPage());
        dto.setFontSize(workItem.getFontSize());
        dto.setFontStyle(workItem.getFontStyle());

        if (workItem.getSign() != null) {
            dto.setSign(toSignDto(workItem.getSign()));
        } else {
            dto.setSign(null);
        }

        return dto;
    }

    public static WorkItem toEntity(WorkItemDto dto, Work work, User user, Sign sign) {
        WorkItem workItem = new WorkItem();
        workItem.setId(dto.getId());
        workItem.setWork(work);
        workItem.setUser(user);

        if (sign != null) {
            workItem.setSign(sign);
        } else {
            workItem.setSign(null);
        }

        workItem.setType(dto.getType());
        workItem.setText(dto.getText());
        workItem.setXPosition(dto.getXPosition());
        workItem.setYPosition(dto.getYPosition());
        workItem.setWidth(dto.getWidth());
        workItem.setHeight(dto.getHeight());
        workItem.setFree(dto.getFree());
        workItem.setPage(dto.getPage());
        workItem.setFontSize(dto.getFontSize());
        workItem.setFontStyle(dto.getFontStyle());

        return workItem;
    }

    // Sign 객체를 SignDto로 변환하는 메소드로 분리하여 코드의 중복을 줄이고 가독성을 높입니다.
    private SignDto toSignDto(Sign sign) {
        SignDto signDto = new SignDto();
        signDto.setId(sign.getId());
        // 필요한 다른 필드들도 설정
        signDto.setUserId(sign.getUser() != null ? sign.getUser().getId() : null);
        signDto.setPath(sign.getPath());
        signDto.setSaved(sign.isSaved());
        signDto.setDeleted(sign.isDeleted());
        signDto.setCreateDate(sign.getCreateDate());
        signDto.setUpdateDate(sign.getUpdateDate());
        return signDto;
    }
}
