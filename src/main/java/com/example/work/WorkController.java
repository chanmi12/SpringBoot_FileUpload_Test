package com.example.work;

import com.example.work.WorkDto;
import com.example.work.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")

@CrossOrigin(origins = {"http://localhost:3000", "https://oursign.vercel.app/"})
@RequiredArgsConstructor
public class WorkController{

    private final WorkService workService;
    //파일업로드 (create)
    @PostMapping("/{userId}/works/upload")
    public ResponseEntity<String> uploadWork(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            String message = workService.uploadWork(userId, file, name);
            return ResponseEntity.ok("Work uploaded successfully. Work ID: " + message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        }
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

    //Work 부분 수정 ( Update )
    @PutMapping("/{userId}/works/{workId}/update")
    public ResponseEntity<WorkDto> updateWork(@PathVariable Long userId,
                                              @PathVariable Long workId,
                                              @RequestBody WorkDto workDto) {
        WorkDto updatedWork = workService.updateWork(workId, userId, workDto);
        return ResponseEntity.ok(updatedWork);
    }

    //Work 파일 수정 ( Update )
    @PutMapping("/{userId}/works/{workId}/updateFile")
    public ResponseEntity<String> updateWorkFile(@PathVariable Long userId,
                                                 @PathVariable Long workId,
                                                 @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        String fileUrl = workService.updateWorkFile(workId, userId, file);
        return ResponseEntity.ok("File updated successfully. New File URL: " + fileUrl);
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
//    @GetMapping(value = "/works/all", produces = "application/json")
    public ResponseEntity<List<WorkDto>> getAllWorks() {
        List<WorkDto> works = workService.getAllWorks();
        return ResponseEntity.ok(works);
    }

    //휴지통
    @PutMapping("/{userId}/works/{workId}/trashedOn") //작업을 휴지통으로 이동
    public ResponseEntity<String> markOnWokrTrashed(@PathVariable Long userId, @PathVariable Long workId){
        workService.markOnWorkTrashed(userId, workId);
        return ResponseEntity.ok("Work ID " + workId + " moved to trashed.");
    }
    @PutMapping("/{userId}/works/{workId}/trashedOff") //작업을 휴지통에서 복원
    public ResponseEntity<String> markOffWorkTrashed(@PathVariable Long userId, @PathVariable Long workId) {
        workService.markOffWorkTrashed(userId, workId);
        return ResponseEntity.ok("Work ID " + workId + " restored from trashed.");
    }

    @GetMapping("/{userId}/works/trashed") //휴지통 조회
    public ResponseEntity<List<WorkDto>> getTrashedWork(@PathVariable Long userId){
        List<WorkDto> trashedWorks = workService.getTrashedWorksByUserId(userId);
        return ResponseEntity.ok(trashedWorks);
    }

    @GetMapping("/{userId}/works/NotTrashed") //휴지통이 아닌 작업 조회
    public ResponseEntity<List<WorkDto>> getNotTrashedWork(@PathVariable Long userId){
        List<WorkDto> trashedWorks = workService.getNotTrashedWorksByUserId(userId);
        return ResponseEntity.ok(trashedWorks);
    }
// 공유
@PostMapping("/{workId}/updateSharedStatus") //공유 상태 변경
    public ResponseEntity<Void> updateSharedStatus(@PathVariable Long userId, @PathVariable Long workId) {
        workService.updateWorkSharedStatus(workId, userId);
        return ResponseEntity.ok().build();
    }
    //생성자는 자신이며 , 공유된 작업 조회
    @GetMapping("/{userId}/works/shared")
    public ResponseEntity<List<WorkDto>> getSharedWorks(@PathVariable Long userId) {
        List<WorkDto> sharedWorks = workService.getSharedWorksByUserId(userId);
        return ResponseEntity.ok(sharedWorks);
    }
    //생성자 상관없이, 공유된 작업 조회
    @GetMapping("/{userId}/works/sharedWithMe")
    public ResponseEntity<List<WorkWithStatusDto>> getWorksSharedWithUser(@PathVariable Long userId) {
        List<WorkWithStatusDto> sharedWorks = workService.getWorksSharedWithUserNotTrashed(userId);
        return ResponseEntity.ok(sharedWorks);
    }
    //작업 상세 조회
    @GetMapping("/works/{workId}/details")
    public ResponseEntity<WorkDto> getWorkDetails(@PathVariable Long workId) {
        WorkDto workDto = workService.getWorkDetails(workId);
        return ResponseEntity.ok(workDto);
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

