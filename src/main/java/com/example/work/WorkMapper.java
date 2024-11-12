package com.example.work;

import org.springframework.stereotype.Component;

@Component
public class WorkMapper {

    //WorkDto 객체를 Work 객체로 변환

    public WorkDto toDto(Work work, int userCount) {
        if (work == null) {
            return null;
        }
        return new WorkDto(
                work.getId(),
                work.getUserId(),
                work.getName(),
                work.getPath(),
                work.getXSize(),
                work.getYSize(),
                work.getShared(),
                work.getTrashed(),
                work.getFinish(),
                work.getCreateDate(),
                work.getUpdateDate(),
                work.getOpenDate(),
                work.getDeleteDate(),
                userCount // userCount 설정,
        );
    }
    public WorkWithStatusDto toWorkWithStatusDto(Work work, int userCount, boolean  userFinished) {
        if (work == null) {
            return null;
        }
        return new WorkWithStatusDto(
                work.getId(),
                work.getUserId(),
                work.getName(),
                work.getPath(),
                work.getXSize(),
                work.getYSize(),
                work.getShared(),
                work.getTrashed(),
                work.getFinish(),
                work.getCreateDate(),
                work.getUpdateDate(),
                work.getOpenDate(),
                work.getDeleteDate(),
                userCount,
                userFinished
        );
    }


    public static Work toEntity(WorkDto workDto) {
        if (workDto == null) {
            return null;
        }
        Work work = new Work();
        work.setId(workDto.getId());
        work.setUserId(workDto.getUserId());
        work.setName(workDto.getName());
        work.setPath(workDto.getPath());
        work.setXSize(workDto.getXSize());
        work.setYSize(workDto.getYSize());
        work.setShared(workDto.getShared());
        work.setTrashed(workDto.getTrashed());
        work.setFinish(workDto.getFinish());
        work.setCreateDate(workDto.getCreateDate());
        work.setUpdateDate(workDto.getUpdateDate());
        work.setOpenDate(workDto.getOpenDate());
        work.setDeleteDate(workDto.getDeleteDate());
        return work;
    }

    public void updateEntityFromDto(WorkDto workDto, Work existingWork) {
        if (workDto.getName() != null) {
            existingWork.setName(workDto.getName());
        }
        if (workDto.getPath() != null) {
            existingWork.setPath(workDto.getPath());
        }
        if (workDto.getXSize() != null) {
            existingWork.setXSize(workDto.getXSize());
        }
        if (workDto.getYSize() != null) {
            existingWork.setYSize(workDto.getYSize());
        }
        if (workDto.getShared() != null) {
            existingWork.setShared(workDto.getShared());
        }
        if (workDto.getTrashed() != null) {
            existingWork.setTrashed(workDto.getTrashed());
        }
        if (workDto.getFinish() != null) {
            existingWork.setFinish(workDto.getFinish());
        }
        if (workDto.getCreateDate() != null) {
            existingWork.setCreateDate(workDto.getCreateDate());
        }
        if (workDto.getUpdateDate() != null) {
            existingWork.setUpdateDate(workDto.getUpdateDate());
        }
        if (workDto.getOpenDate() != null) {
            existingWork.setOpenDate(workDto.getOpenDate());
        }
        if (workDto.getDeleteDate() != null) {
            existingWork.setDeleteDate(workDto.getDeleteDate());
        }
    }
}

