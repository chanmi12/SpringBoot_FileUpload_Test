package com.example.work.service;

import com.example.work.WorkDto;
import com.example.work.WorkMapper;
import com.example.work.entity.Work;
import com.example.work.repository.WorkRepository;
import com.example.workItem.WorkItem;
import com.example.workItem.WorkItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public String uploadWork(Long userId, MultipartFile file, String name ){
        String fileUrl = awsS3Service.uploadFile(file);

        Work work = new Work();
        work.setUserId(userId);
        work.setName(name);
        work.setPath(fileUrl);

        workRepository.save(work);
        return "File upload successfully. Work ID: " + work.getId();
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
    //Work 수정
    public String updateWork(Long userId, Long workId, MultipartFile file, String name) {
        Optional<Work> workOpt = workRepository.findByIdAndUserId(workId, userId);
        if (workOpt.isPresent()) {
            Work work = workOpt.get();

            awsS3Service.deleteFileFromS3(work.getPath()); // Delete old file
            String newFileUrl = awsS3Service.uploadFile(file);

            work.setName(name);
            work.setPath(newFileUrl);

            workRepository.save(work);
            return "Work ID: " + workId + " updated successfully.";
        }
        return null;
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
}
