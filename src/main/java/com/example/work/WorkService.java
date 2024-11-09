package com.example.work;

import com.example.awsS3.AwsS3Service;
import com.example.workItem.WorkItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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
    @Autowired
    WorkItemRepository workItemRepository;
    //Work 업로드
    public String uploadWork(Long userId, MultipartFile file, String name) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        String fileUrl = awsS3Service.uploadFile("file", file);
        Work work = new Work();
        work.setUserId(userId);
        work.setName(name);
        work.setPath(fileUrl);
        workRepository.save(work);

        return "File uploaded successfully. Work ID: " + work.getId();
    }
    //Work에서 User의 수를 반환 하는 함수
    private int getUserCount(Long workId) {
        return workItemRepository. countDistinctUsersByWorkId(workId);
    }

    //유저의 Work 목록 조회
    public List<WorkDto> getUserWorks(Long userId) {
        List<Work> works = workRepository.findByUserId(userId);
        return works.stream()
                .map(work -> workMapper.toDto(work, getUserCount(work.getId())))
                .collect(Collectors.toList());
    }

    //유저의 Work 조회
    public WorkDto getWorkByIdAndUserId(Long userId, Long workId) {
        Optional<Work> workOpt = workRepository.findByIdAndUserId(workId, userId);
        return workOpt.map(work -> workMapper.toDto(work, getUserCount(work.getId()))).orElse(null);
    }

    //Work 파일 업데이트
    public String updateWorkFile(Long workId, Long userId, MultipartFile file) {
        //Work ID와 User ID로 Work 찾기
        Optional<Work> workOpt = workRepository.findByIdAndUserId(workId, userId);
        if (!workOpt.isPresent()) {
            throw new IllegalArgumentException("Work not found for given id and user");
        }
        //Work 업데이트
        Work existingWork = workOpt.get();
        //기존 파일 삭제
        awsS3Service.deleteFileFromS3(existingWork.getPath());
        //새로운 파일 업로드
        String newFileUrl = awsS3Service.uploadFile("file", file);
        //Work 업데이트
        existingWork.setPath(newFileUrl);
        //Work 저장
        workRepository.save(existingWork);

        return newFileUrl;
    }

    //Work 삭제
    public String deleteWork(Long userId, Long workId) {
        Optional<Work> workOpt = workRepository.findByIdAndUserId(workId, userId);
        if (workOpt.isPresent()) {
            Work work = workOpt.get();
            awsS3Service.deleteFileFromS3(work.getPath());
            workRepository.deleteById(workId);
            return "Work ID " + workId + " deleted successfully.";
        }
        return null;
    }

    //모든 Work 조회
    public List<WorkDto> getAllWorks() {
        List<Work> works = workRepository.findAll();
        return works.stream()
                .map(work -> workMapper.toDto(work, getUserCount(work.getId())))
                .collect(Collectors.toList());
    }


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
        return workMapper.toDto(existingWork, getUserCount(existingWork.getId()));
    }

    //휴지통
    @Transactional
    public void markOnWorkTrashed(Long userId, Long workId) { // 휴지통으로 이동
        Optional<Work> workOpt = workRepository.findByIdAndUserId(workId, userId);
        if (!workOpt.isPresent()) {
            throw new IllegalArgumentException("Work not found for given id and user" + workId);
        }
        Work work = workOpt.get();
        work.setTrashed(true);
        work.setDeleteDate(LocalDateTime.now());
        workRepository.save(work);
    }

    @Transactional
    public void markOffWorkTrashed(Long userId, Long workId) { // 휴지통에서 복구
        Optional<Work> workOpt = workRepository.findByIdAndUserId(workId, userId);
        if (!workOpt.isPresent()) {
            throw new IllegalArgumentException("Work not found for given id and user" + workId);
        }
        Work work = workOpt.get();
        work.setTrashed(false);
        workRepository.save(work);
    }

    @Transactional
    public List<WorkDto> getTrashedWorksByUserId(Long userId) { // 휴지통 조회
        List<Work> trashedWorks = workRepository.findByUserIdAndTrashedTrue(userId);
        return trashedWorks.stream()
                .map(work -> workMapper.toDto(work, getUserCount(work.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<WorkDto> getNotTrashedWorksByUserId(Long userId) { // 휴지통에 없는 Work 조회
        List<Work> notTrashedWorks = workRepository.findByUserIdAndTrashedFalse(userId);
        return notTrashedWorks.stream()
                .map(work -> workMapper.toDto(work, getUserCount(work.getId())))
                .collect(Collectors.toList());
    }

    //공유
    //Work 공유 상태 변경
    @Transactional
    public void updateWorkSharedStatus(Long workId, Long userId){
        Optional<Work> workOpt = workRepository.findByIdAndUserId(workId, userId);
        if(!workOpt.isPresent()){
            throw new IllegalArgumentException("Work not found for given id and user");
        }
    Work work = workOpt.get();

        //특정 Work에 대한 모든 User ID를 반환하는 쿼리
        List<Long> distinctUserIds = workItemRepository.findDistinctUserIdsByWorkId(workId);
        work.setShared(distinctUserIds.size() > 1);

        workRepository.save(work);
    }

    //생성자는 자신이다 공유된 Work 조회
    @Transactional
    public List<WorkDto> getSharedWorksByUserId(Long userId) {
        List<Work> sharedWorks = workRepository.findByUserIdAndSharedTrue(userId);
        return sharedWorks.stream()
                .map(work -> workMapper.toDto(work, getUserCount(work.getId())))
                .collect(Collectors.toList());
    }
    //생성자는 상관이 없다. 공유된 Work 조회
    @Transactional
    public List<WorkDto> getWorksSharedWithUserNotTrashed(Long userId) {
        List<Work> worksSharedWithUser = workRepository.findWorksSharedWithUserNotTrashed(userId);
        return worksSharedWithUser.stream()
                .map(work -> workMapper.toDto(work, getUserCount(work.getId())))
                .collect(Collectors.toList());
    }
    //Work 상세 조회
    public WorkDto getWorkDetails(Long workId) {
        Optional<Work> workOpt = workRepository.findById(workId);
        if (!workOpt.isPresent()) {
            throw new IllegalArgumentException("Work not found for given id");
        }
        Work work = workOpt.get();
        return workMapper.toDto(work, getUserCount(work.getId()));
    }

}