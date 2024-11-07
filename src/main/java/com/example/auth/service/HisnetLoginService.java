package com.example.auth.service;//package com.example.auth.service;
//
//import com.example.auth.dto.AuthDto;
//import com.example.auth.exception.FailedHisnetLoginException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpStatusCodeException;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponents;
//import org.springframework.web.util.UriComponentsBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class HisnetLoginService {
//
////    @Value("${hisnet.access-key}")
////    private String accessKey;
////
////    public AuthDto callHisnetLoginApi(AuthDto dto) {
////        Map<String, Object> requestBody = new HashMap<>();
////
////        requestBody.put("token", dto.getHisnetToken());
////        requestBody.put("accessKey", accessKey);
////
////        HttpHeaders headers = new HttpHeaders();
////        headers.setContentType(MediaType.APPLICATION_JSON);
////
////        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
////
////        RestTemplate restTemplate = new RestTemplate();
////        String url = "https://walab.info:8443/HisnetLogin/api/hisnet/login/validate";
////        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
////
////        try {
////            ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<>() {};
////            ResponseEntity<Map<String, Object>> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, typeRef);
////            Map<String, Object> result = resultMap.getBody();
////            assert result != null;
////            return AuthDto.builder()
////                    .uniqueId(result.get("uniqueId").toString())
////                    .name(result.get("name").toString())
////                    .email(result.get("email").toString())
////                    .department(result.get("department").toString())
////                    .major1(result.get("major1").toString())
////                    .major2(result.get("major2").toString())
////                    .grade(Integer.parseInt(result.get("grade").toString()))
////                    .semester(Integer.parseInt(result.get("semester").toString()))
////                    .build();
////        } catch (HttpStatusCodeException e) {
////            Map<String, Object> result = new HashMap<>();
////            try {
////                result = new ObjectMapper().readValue(e.getResponseBodyAsString(), new TypeReference<>() {});
////            } catch (Exception ex) {
////                throw new FailedHisnetLoginException("예상치 못한 변수 발생", 500);
////            }
////            throw new FailedHisnetLoginException(
////                    result.get("message").toString(), e.getStatusCode().value());
////        }
////    }
//
//    @Value("${hisnet.access-key}")
//    private String accessKey;
//
//    public AuthDto callHisnetLoginApi(String token) {
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("token", token);
//        requestBody.put("accessKey", accessKey);
//
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "https://walab.info:8443/HisnetLogin/api/hisnet/login/validate";
//
//
//        try {
//            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
//            });
//            Map<String, Object> responseBody = response.getBody();
//
//            return AuthDto.builder()
//                    .uniqueId(responseBody.get("uniqueId").toString())
//                    .name(responseBody.get("name").toString())
//                    .email(responseBody.get("email").toString())
//                    .department(responseBody.get("department").toString())
//                    .major1(responseBody.get("major1").toString())
//                    .major2(responseBody.get("major2").toString())
//                    .grade(Integer.parseInt(responseBody.get("grade").toString()))
//                    .semester(Integer.parseInt(responseBody.get("semester").toString()))
//                    .build();
//
//        } catch (HttpStatusCodeException e) {
//            Map<String, Object> result = new HashMap<>();
//            try {
//                result = new ObjectMapper().readValue(e.getResponseBodyAsString(), new TypeReference<>() {
//                });
//            } catch (Exception ex) {
//                throw new FailedHisnetLoginException("예상치 못한 변수 발생", 500);
//            }
//            throw new FailedHisnetLoginException(
//                    result.get("message").toString(), e.getStatusCode().value());
//        }
//    }
//
//}

import com.example.auth.dto.AuthDto;
import com.example.auth.exception.FailedHisnetLoginException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class HisnetLoginService {

    @Value("${hisnet.access-key}")
    private String accessKey;

    public AuthDto callHisnetLoginApi(String token) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("token", token);
        requestBody.put("accessKey", accessKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://walab.info:8443/HisnetLogin/api/hisnet/login/validate";

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
            });
            Map<String, Object> responseBody = response.getBody();

            return AuthDto.builder()
                    .uniqueId(responseBody.get("uniqueId").toString())
                    .name(responseBody.get("name").toString())
                    .email(responseBody.get("email").toString())
                    .department(responseBody.get("department").toString())
                    .major1(responseBody.get("major1").toString())
                    .major2(responseBody.get("major2").toString())
                    .grade(Integer.parseInt(responseBody.get("grade").toString()))
                    .semester(Integer.parseInt(responseBody.get("semester").toString()))
                    .build();

        } catch (HttpStatusCodeException e) {
            Map<String, Object> result = new HashMap<>();
            try {
                result = new ObjectMapper().readValue(e.getResponseBodyAsString(), new TypeReference<>() {
                });
            } catch (Exception ex) {
                throw new FailedHisnetLoginException("예상치 못한 변수 발생", 500);
            }
            throw new FailedHisnetLoginException(result.get("message").toString(), e.getStatusCode().value());
        }
    }
}