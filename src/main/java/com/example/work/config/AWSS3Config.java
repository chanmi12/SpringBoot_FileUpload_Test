package com.example.work.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration //AWS S3에 접근하기 위한 설정
public class AWSS3Config {

    //application.properties에 설정한 AWS S3의 정보를 가져와서 AmazonS3 객체를 생성
    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;
    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean// Bean으로 AmazonS3 객체를 생성
    public AmazonS3 amazonS3Client(){ //AmazonS3 객체를 생성
        BasicAWSCredentials awsCredentials= new BasicAWSCredentials(accessKey, secretKey); //AWS S3에 접근하기 위한 인증 정보
       //AmazonS3Client 객체를 생성하고 반환
        return (AmazonS3Client) AmazonS3ClientBuilder.standard() //AmazonS3Client 객체 생성
                .withRegion(region) //지역 설정
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)) //인증 정보 설정
                .build(); //객체 생성
    }


}
