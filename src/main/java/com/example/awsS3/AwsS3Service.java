package com.example.awsS3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
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
    //파일 다운로드
    public File downloadFile(String bucketName, String key) throws IOException {
        S3Object s3Object = amazonS3.getObject(bucketName, key);
        InputStream inputStream = s3Object.getObjectContent();
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + key);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] read_buf = new byte[1024];
            int read_len;
            while ((read_len = inputStream.read(read_buf)) > 0) {
                outputStream.write(read_buf, 0, read_len);
            }
        }
        return file;
    }
    public String extractKeyFromUrl(String url) {
        String bucketUrl = "https://swteam24-significant.s3.ap-northeast-2.amazonaws.com/";
        return url.replace(bucketUrl, "");
    }
    public InputStream getFileAsStream(String s3Url) {
        String key = extractKeyFromUrl(s3Url);  // URL에서 키만 추출
        try {
            S3Object s3Object = amazonS3.getObject(bucket, key);
            return s3Object.getObjectContent();
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "S3에 해당 파일이 존재하지 않습니다: " + key);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 불러오는 중 오류가 발생했습니다.");
        }
    }

}
