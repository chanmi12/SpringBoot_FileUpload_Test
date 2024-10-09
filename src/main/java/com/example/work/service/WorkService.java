package com.example.work.service;

import com.example.work.WorkDto;
import com.example.work.WorkMapper;
import com.example.work.entity.Work;
import com.example.work.repository.WorkRepository;
import com.example.workItem.WorkItem;
import com.example.workItem.WorkItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkService {
    @Autowired
    private WorkRepository workRepository;
    @Autowired
    private AwsS3Service awsS3Service;
    @Autowired
    private WorkMapper workMapper;

    //Work 업로드
public String uploadWork(Long userId, MultipartFile file, String name) {
    if(file.isEmpty()){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
    }
    //파일 업로드
        String fileUrl = awsS3Service.uploadFile("file", file);
        Work work = new Work();
        work.setUserId(userId);
        work.setName(name);
        work.setPath(fileUrl);
        workRepository.save(work);

        return "File uploaded successfully. Work ID: " + work.getId();
}
    //유저의 Work 목록 조회
    public List<WorkDto> getUserWorks(Long userId){
        List<Work> works = workRepository.findByUserId(userId);
        return works.stream()
                .map(workMapper::toDto)
                .collect(Collectors.toList());
    }
    //유저의 Work 조회
    public WorkDto getWorkByIdAndUserId(Long userId, Long workId){
        Optional<Work> workOpt = workRepository.findByIdAndUserId(workId, userId);
        return workOpt.map(workMapper::toDto).orElse(null);
    }
    //Work 파일 업데이트
    public String updateWorkFile(Long workId, Long userId, MultipartFile file) {
        Optional<Work> workOpt = workRepository.findByIdAndUserId(workId, userId);
        if (!workOpt.isPresent()) {
            throw new IllegalArgumentException("Work not found for given id and user");
        }
        Work existingWork = workOpt.get();

        // Remove the old file from S3
        awsS3Service.deleteFileFromS3(existingWork.getPath());

        // Upload the new file and update the path
        String newFileUrl = awsS3Service.uploadFile("file", file);
        existingWork.setPath(newFileUrl);
        workRepository.save(existingWork);

        return newFileUrl;
    }

    //Work 삭제
    public String deleteWork(Long userId, Long workId) {
        Optional<Work> workOpt = workRepository.findByIdAndUserId(workId, userId);
        if (workOpt.isPresent()) {
            Work work = workOpt.get();

            awsS3Service.deleteFileFromS3(work.getPath()); // Delete from S3
            workRepository.deleteById(workId); // Delete from DB

            return "Work ID " + workId + " deleted successfully.";
        }
        return null;
    }

    //모든 Work 조회
    public List<WorkDto> getAllWorks() {
        List<Work> works = workRepository.findAll();
        return works.stream()
                .map(workMapper::toDto)
                .collect(Collectors.toList());
    }

//    //Work 공유 상태 변경
//    public String updateWorkSharedStatus (Long userId , Long workId, boolean sharedStatus){
//        Optional<Work> workOpt = workRepository.findByIdAndUserId(workId, userId);
//        if (workOpt.isPresent()) {
//
//            Work work = workOpt.get();
//            work.setShared(sharedStatus);
//            workRepository.save(work);
//            return "Work ID: " + workId + " shared status updated to : " + sharedStatus;
//        }else{
//            throw new IllegalArgumentException("Work ID: " + workId + " not found");
//        }
//    }

    //Work 부분 수정
    @Transactional
    public WorkDto updateWork(Long workId, Long userId, WorkDto workDto) {
        Optional<Work> workOpt = workRepository.findByIdAndUserId(workId, userId);
        if (!workOpt.isPresent()) {
            throw new IllegalArgumentException("Work not found for given id and user");
        }
        Work existingWork = workOpt.get();

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

        workRepository.save(existingWork);
        return workMapper.toDto(existingWork);
    }


}
