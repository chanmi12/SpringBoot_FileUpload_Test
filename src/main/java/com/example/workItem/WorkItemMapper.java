package com.example.workItem;

import org.springframework.stereotype.Component;

@Component
public class WorkItemMapper {
    public WorkItemDto toDto(WorkItem workItem) {
        return new WorkItemDto(
                workItem.getId(),
                workItem.getWork().getId(),
                workItem.getSignId(),
                workItem.getUserId(),
                workItem.getType(),
                workItem.getText(),
                workItem.getXPosition(),
                workItem.getYPosition(),
                workItem.getWidth(),
                workItem.getHeight(),
                workItem.getCreateDate(),
                workItem.getUpdateDate(),
                workItem.getFree(),
                workItem.getPage(),
                workItem.getFontSize(),
                workItem.getFontStyle()
        );
    }

    public WorkItem toEntity(WorkItemDto workItemDto, WorkItem workItem) {
        workItem.setSignId(workItemDto.getSignId());
        workItem.setUserId(workItemDto.getUserId());
        workItem.setType(workItemDto.getType());
        workItem.setText(workItemDto.getText());
        workItem.setXPosition(workItemDto.getXPosition());
        workItem.setYPosition(workItemDto.getYPosition());
        workItem.setWidth(workItemDto.getWidth());
        workItem.setHeight(workItemDto.getHeight());
        workItem.setFree(workItemDto.getFree());
        workItem.setPage(workItemDto.getPage());
        workItem.setFontSize(workItemDto.getFontSize());
        workItem.setFontStyle(workItemDto.getFontStyle());
        return workItem;
    }
}
