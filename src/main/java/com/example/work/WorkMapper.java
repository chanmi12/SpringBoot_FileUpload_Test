package com.example.work;

import com.example.work.entity.Work;

public class WorkMapper {
    public static WorkDto toDto(Work work) {
        WorkDto workDto = new WorkDto();
        workDto.setId(work.getId());
        workDto.setUserId(work.getUserId());
        workDto.setName(work.getName());
        workDto.setPath(work.getPath());
        workDto.setXSize(work.getXSize());
        workDto.setYSize(work.getYSize());
        workDto.setShared(work.getShared());
        workDto.setTrashed(work.getTrashed());
        workDto.setFinish(work.getFinish());
        workDto.setCreateDate(work.getCreateDate());
        workDto.setUpdateDate(work.getUpdateDate());
        workDto.setOpenDate(work.getOpenDate());
        return workDto;
    }
}

