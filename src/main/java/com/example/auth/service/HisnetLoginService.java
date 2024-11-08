package com.example.auth.service;

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