package com.example.workItem;


import com.example.sign.Sign;
import com.example.sign.SignDto;
import com.example.sign.SignMapper;
import com.example.user.User;
import com.example.work.Work;
import org.springframework.beans.factory.annotation.Autowired;
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
        dto.setFinished(workItem.getFinished());
        dto.setPage(workItem.getPage());
        dto.setFontSize(workItem.getFontSize());
        dto.setFontStyle(workItem.getFontStyle());
        dto.setAutoCreated(workItem.getAutoCreated());
        // Map signId if Sign is not null and populate full Sign details
        if (workItem.getSign() != null) {
            dto.setSignId(workItem.getSign().getId());
            dto.setSign(toSignDto(workItem.getSign()));
        } else {
            dto.setSignId(null);
            dto.setSign(null);
        }

        return dto;
    }

    public static WorkItem toEntity(WorkItemDto dto, Work work, User user, Sign sign) {
        WorkItem workItem = new WorkItem();
        workItem.setId(dto.getId());
        workItem.setWork(work);
        workItem.setUser(user);

        // Conditionally set Sign based on type (type = 1 or 3)
        if (dto.getType() == 1 || dto.getType() == 3) {
            workItem.setSign(sign != null ? sign : null);
        }

        workItem.setType(dto.getType());
        workItem.setText(dto.getText());
        workItem.setXPosition(dto.getXPosition());
        workItem.setYPosition(dto.getYPosition());
        workItem.setWidth(dto.getWidth());
        workItem.setHeight(dto.getHeight());
        workItem.setFree(dto.getFree());
        workItem.setFinished(dto.getFinished());
        workItem.setPage(dto.getPage());
        workItem.setFontSize(dto.getFontSize());
        workItem.setFontStyle(dto.getFontStyle());
        workItem.setAutoCreated(dto.getAutoCreated());
        return workItem;
    }

    private SignDto toSignDto(Sign sign) {
        SignDto signDto = new SignDto();
        signDto.setId(sign.getId());
        signDto.setUserId(sign.getUser() != null ? sign.getUser().getId() : null);
        signDto.setPath(sign.getPath());
        signDto.setSaved(sign.isSaved());
        signDto.setDeleted(sign.isDeleted());
        signDto.setCreateDate(sign.getCreateDate());
        signDto.setUpdateDate(sign.getUpdateDate());
        return signDto;
    }
}