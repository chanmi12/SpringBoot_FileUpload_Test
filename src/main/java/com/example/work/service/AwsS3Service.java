package com.example.work.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsS3Service { //파일을 AWS S3에 업로드하고 URL을 반환하는 서비스

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;//AWS S3 버킷 이름

    private final AmazonS3 amazonS3; //AWS S3 객체 [ @Configuration에서 생성한 Bean ]

    private String createFileName(String fileName){ //파일 이름을 생성하는 메소드
        return UUID.randomUUID().toString().concat(getFileExtension(fileName)); //UUID를 이용해 파일 이름 생성
    }//UUID를 String으로 변환 , 파라미터로 받은 파일 이름에 확장자를 붙여서 반환

    public String uploadFile(MultipartFile file){//파일을 업로드하고 URL을 반환하는 메소드
        String fileName = createFileName(file.getOriginalFilename()); //파일 이름 생성
        ObjectMetadata objectMetadata = new ObjectMetadata(); //파일 메타데이터 생성
        objectMetadata.setContentLength(file.getSize()); //파일 크기 설정
        objectMetadata.setContentType(file.getContentType()); //파일 타입 설정

        String key = fileName+"_"+file.getOriginalFilename(); //파일 이름과 원래 파일 이름을 합쳐서 key 생성
        try(InputStream inputStream= file.getInputStream()){ //파일을 읽어오는 InputStream 객체 생성
            amazonS3.putObject(new PutObjectRequest (bucket, key, inputStream, objectMetadata)//파일을 AWS S3에 업로드
                    .withCannedAcl(CannedAccessControlList.PublicRead));//파일 접근 권한 설정 (PublicRead)
        }catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }
        return amazonS3.getUrl(bucket, key).toString();//파일 URL 반환
    }

    private String getFileExtension(String fileName){ //파일의 확장자를 반환하는 메소드
        try{
            return fileName.substring(fileName.lastIndexOf(".")); //파일 이름에서 마지막 .의 위치부터 끝까지 반환
        }catch (StringIndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형시의 파일("+fileName+")입니다.");//예외 처리
        }
    }

    public void deleteFileFromS3(String fileUrl){
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
        amazonS3.deleteObject(bucket, fileName);
    }
}
