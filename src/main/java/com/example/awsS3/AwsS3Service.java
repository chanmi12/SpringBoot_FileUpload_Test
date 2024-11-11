package com.example.awsS3;

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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsS3Service { //파일을 AWS S3에 업로드하고 URL을 반환하는 서비스

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;//AWS S3 버킷 이름

    private final AmazonS3 amazonS3; //AWS S3 객체 [ @Configuration에서 생성한 Bean ]

    private String createFileName(String originalFilename) {
        return System.currentTimeMillis() + "_" + originalFilename;
    }

    public String uploadFile(String folder, MultipartFile file){
        String fileName = createFileName(file.getOriginalFilename());
        // 파일 이름을 UUID로 생성
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        String key = folder + "/" + encodeFileName(fileName);

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed.");
        }
        return amazonS3.getUrl(bucket, key).toString();
    }

    private String getFileExtension(String fileName){ //파일의 확장자를 반환하는 메소드
        try{
            return fileName.substring(fileName.lastIndexOf(".")); //파일 이름에서 마지막 .의 위치부터 끝까지 반환
        }catch (StringIndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형시의 파일("+fileName+")입니다.");//예외 처리
        }
    }

    private String encodeFileName(String fileName) {
        try {
            return URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File name encoding failed.");
        }
    }
    public void deleteFileFromS3(String fileUrl){
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
        amazonS3.deleteObject(bucket, fileName);
    }
}
