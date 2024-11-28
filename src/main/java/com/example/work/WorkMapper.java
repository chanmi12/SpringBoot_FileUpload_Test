package com.example.work;

import com.example.itext.Itext;
import com.example.itext.ItextDto;
import org.springframework.stereotype.Component;
import com.example.itext.ItextMapper;
@Component
public class WorkMapper {

    //WorkDto 객체를 Work 객체로 변환
    private final ItextMapper itextMapper;

    public WorkMapper(ItextMapper itextMapper) {
        this.itextMapper = itextMapper;
    }

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
                userCount,
                work.getItext() != null ? itextMapper.toDto(work.getItext()) : null // Map Itext if present
        );
    }

    public WorkWithStatusDto toWorkWithStatusDto(Work work, int userCount, boolean userFinished) {
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

    public Work toEntity(WorkDto workDto) {
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
        if (workDto.getItext() != null) {
            Itext itext = itextMapper.toEntity(workDto.getItext(), work);
            work.setItext(itext);
        }
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
        if (workDto.getOpenDate() != null) {
            existingWork.setOpenDate(workDto.getOpenDate());
        }
        if (workDto.getDeleteDate() != null) {
            existingWork.setDeleteDate(workDto.getDeleteDate());
        }
        if (workDto.getItext() != null) {
            // Map Itext only if it's not null
            Itext itextEntity = itextMapper.toEntity(workDto.getItext(), existingWork); // Pass the associated Work
            existingWork.setItext(itextEntity);
        }
    }
}

