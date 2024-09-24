package com.example.work.controller;

import com.example.work.repository.WorkRepository;
import com.example.work.entity.Work;
import com.example.work.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

//@RestController //REST API를 처리하는 컨트롤러
//@RequestMapping("/api/works") //URL 매핑
//public class WorkController { //파일 업로드를 처리하는 컨트롤러
//
//    @Autowired
//    private AwsS3Service awsS3Service; //AWS S3에 파일을 업로드하는 서비스
//
//    @Autowired
//    private WorkRepository workRepository; //Work 엔티티를 저장하는 레포지토리
//
//    @PostMapping("/upload")//파일 업로드 요청을 처리하는 메소드
//    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,//파일 업로드 요청을 처리하는 메소드
//                                             @RequestParam("userId") Long userId,//파라미터로 받은 파일, 사용자 ID, 이름, 크기를 Work 엔티티에 저장
//                                             @RequestParam("name") String name,
//                                             @RequestParam(value = "xSize", required = false) Integer xSize,
//                                             @RequestParam(value = "ySize", required = false) Integer ySize)
//    {
//        if(file.isEmpty()){//파일이 비어있는지 확인
//            return ResponseEntity.badRequest().body("File is empty");
//        }
//        String fileUrl = awsS3Service.uploadFile(file);//파일을 AWS S3에 업로드하고 URL을 반환
//
//        Work work = new Work();//Work 엔티티 생성
//        work.setUserId(userId);//파라미터로 받은 사용자 ID, 이름, 크기를 저장
//        work.setName(name);
//        work.setPath(fileUrl);
//        work.setXSize(xSize);
//        work.setYSize(ySize);
//
//        workRepository.save(work);//Work 엔티티를 저장
//
//        return ResponseEntity.ok("파일이 성공적으로 업로드되었습니다. Work ID: " + work.getId());
//    }
//
//}
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


