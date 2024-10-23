package com.example.workItem;


import com.example.sign.Sign;
import com.example.user.User;
import com.example.work.entity.Work;
import org.springframework.stereotype.Component;



@Component
public class WorkItemMapper {

    public WorkItemDto toDto(WorkItem workItem) {
        WorkItemDto dto = new WorkItemDto();
        dto.setId(workItem.getId());
        dto.setWorkId(workItem.getWork().getId());
        dto.setUserId(workItem.getUser().getId());
        dto.setSignId(workItem.getSign() != null ? workItem.getSign().getId() : null);
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

        return dto;
    }

    public static WorkItem toEntity(WorkItemDto dto, Work work, User user, Sign sign) {
        WorkItem workItem = new WorkItem();
        workItem.setId(dto.getId());
        workItem.setWork(work);
        workItem.setUser(user);

        if(sign!= null){
            workItem.setSign(sign);
        }else{
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


}
