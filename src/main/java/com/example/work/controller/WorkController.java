package com.example.work.controller;

import com.example.work.WorkDto;
import com.example.work.repository.WorkRepository;
import com.example.work.entity.Work;
import com.example.work.service.AwsS3Service;
import com.example.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkController{

    private final WorkService workService;

    //파일업로드 (create)
    @PostMapping ("/{userId}/works/upload")
    public ResponseEntity<String> uploadWork(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name) {

        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("File is empty");
        }

        String message = workService.uploadWork(userId, file, name);

        return ResponseEntity.ok(message);
    }


    // 유저의 파일 목록 조회 (Read)
    @GetMapping("/{userId}/works/list")
    public ResponseEntity<List<WorkDto>>  getUserFiles(@PathVariable Long userId){
        List<WorkDto> works = workService.getUserWorks(userId);
        return ResponseEntity.ok(works);
    }

    //특정 파일 상세 조회 (Read)
    @GetMapping("/{userId}/works/{id}")
    public ResponseEntity<WorkDto> getWorkById(
            @PathVariable Long userId,
            @PathVariable Long id ){

    WorkDto workDto = workService.getWorkByIdAndUserId(userId, id); //WorkService의 getWorkByIdAndUserId 메소드 호출
        return workDto != null //조회 결과에 따라 응답 반환
                ? ResponseEntity.ok(workDto)
                : ResponseEntity.status(404).build();
    }

    //파일 수정 ( Update )
    @PutMapping ("/{userId}/works/{id}/update")
    public ResponseEntity<String> updateWorkFile(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name) {

        String message = workService.updateWork(userId, id, file, name);
        return message != null
                ? ResponseEntity.ok(message)
                : ResponseEntity.status(404).body("Work ID " + id + " not found for user " + userId);
    }

    //파일 삭제 (Delete)
    @DeleteMapping("/{userId}/works/{id}/delete")
    public ResponseEntity<String> deleteWorkFile(
            @PathVariable Long userId,
            @PathVariable Long id) {

        String message = workService.deleteWork(userId, id);
        return message != null
                ? ResponseEntity.ok(message)
                : ResponseEntity.status(404).body("Work ID " + id + " not found for user " + userId);
    }
    //모든 파일 조회
    @GetMapping("/works/all")
    public ResponseEntity<List<WorkDto>> getAllWorks() {
        List<WorkDto> works = workService.getAllWorks();
        return ResponseEntity.ok(works);
    }
}
/* 이전 버전
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkController{

    private final WorkRepository workRepository;
    private final AwsS3Service awsS3Service;

    //파일업로드 (create)
    @PostMapping ("/{userId}/works/upload")
    public ResponseEntity<String> uploadWork(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name){

        if(file.isEmpty()){ //파일이 비어있는지 확인
            return ResponseEntity.badRequest().body("File is empty");
        }

        //새로운 Work Entity 생성
        Work work = new Work();
        work.setUserId(userId); //userId 설정
        work.setName(name); //파일 이름 설정
        String fileUrl = awsS3Service.uploadFile(file); //S3에 파일 업로드
        work.setPath(fileUrl); //파일 경로 설정
        workRepository.save(work); //DB 저장

        return ResponseEntity.ok("파일이 성공적으로 업로드되었습니다. Work ID: " + work.getId());
    }
    // 유저의 파일 목록 조회 (Read)
    @GetMapping("/{userId}/works/list")
    public ResponseEntity<List<Work>> getUserFiles(@PathVariable Long userId){
        List<Work> works = workRepository.findByUserId(userId);
        return ResponseEntity.ok(works);
    }

    //특정 파일 상세 조회 (Read)
    @GetMapping("/{userId}/works/{id}")
    public ResponseEntity<Work> getWorkById(
            @PathVariable Long userId,
            @PathVariable Long id) {

        Optional<Work> workOpt = workRepository.findByIdAndUserId(id, userId);

        if (workOpt.isPresent()) {
            return ResponseEntity.ok(workOpt.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    //파일 수정 ( Update )
    @PutMapping ("/{userId}/works/{id}/update")
    public ResponseEntity<String> updateWorkFile(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name){

        Optional<Work> workOpt = workRepository.findByIdAndUserId(id, userId);

       if(workOpt.isPresent()){
           Work work = workOpt.get();

           //이전파일 삭제
           awsS3Service.deleteFileFromS3(work.getPath());
           //새로운 파일 업로드
           String newFileUrl = awsS3Service.uploadFile(file);
           work.setName(name);
           work.setPath(newFileUrl); //URL 갱신

           workRepository.save(work); //DB 저장

           return ResponseEntity.ok("Work ID: " + id + "가 수정되었습니다.");
       }

        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당유저의 Work ID: " + id + "를 찾을 수 없습니다.");
    }
    //파일 삭제 (Delete)
    @DeleteMapping("/{userId}/works/{id}/delete")
    public ResponseEntity<String> deleteWorkFile(
            @PathVariable Long userId,
            @PathVariable Long id) {

        Optional<Work> workOpt = workRepository.findByIdAndUserId(id, userId);

        if(workOpt.isPresent()){
            Work work = workOpt.get();

            //S3에서 파일 삭제
            awsS3Service.deleteFileFromS3(work.getPath());

            //DB에서 파일 삭제
            workRepository.deleteById(id);

            return ResponseEntity.ok("파일이 성공적으로 삭제되었습니다.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당유저의 Work ID: " + id + "를 찾을 수 없습니다.");
    }

    //모든 파일 조회
    @GetMapping("/works/all")
    public ResponseEntity<List<Work>> getAllWorks(){
        List<Work> works = workRepository.findAll();
        return ResponseEntity.ok(works);
    }
}
*/

