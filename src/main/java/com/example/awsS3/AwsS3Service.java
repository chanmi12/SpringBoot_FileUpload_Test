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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    public String uploadFile(String folder, MultipartFile file) { //파일을 업로드하고 URL을 반환
        String fileName = createFileName(file.getOriginalFilename());
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

    public String encodeFileName(String fileName) {
        try {
            return URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File name encoding failed.");
        }
    }

//
    public void deleteFileFromS3(String fileUrl) {
        String key = decodeKeyFromUrl(fileUrl);
        amazonS3.deleteObject(bucket, key);
    }

    public InputStream getFileAsStream(String s3Path) {
        try {
            String key = decodeKeyFromUrl(s3Path);
            S3Object s3Object = amazonS3.getObject(bucket, key);
            return s3Object.getObjectContent();
        } catch (AmazonS3Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "S3에 해당 파일이 존재하지 않습니다: " + s3Path);
        }
    }

    private String decodeKeyFromUrl(String s3Path) {
        try {
            // Construct the base URL dynamically
            String bucketUrl = amazonS3.getUrl(bucket, "").toString();
            if (s3Path.startsWith(bucketUrl)) {
                String key = s3Path.substring(bucketUrl.length());
                return URLDecoder.decode(key, StandardCharsets.UTF_8.toString());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid S3 file URL format: " + s3Path);
            }
        } catch (UnsupportedEncodingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "URL decoding failed.");
        }
    }
//public String decodeKeyFromUrl(String s3Path) {
//    try {
//        String bucketUrl = amazonS3.getUrl(bucket, "").toString();
//        if (s3Path.startsWith(bucketUrl)) {
//            String key = s3Path.substring(bucketUrl.length());
//            return URLDecoder.decode(key, StandardCharsets.UTF_8.toString());
//        } else {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid S3 file URL format.");
//        }
//    } catch (UnsupportedEncodingException e) {
//        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "URL decoding failed.");
//    }
//}
}
