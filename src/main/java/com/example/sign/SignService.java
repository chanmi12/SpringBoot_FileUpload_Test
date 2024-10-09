package com.example.sign;


import com.example.awsS3.AwsS3Service;
import com.example.user.User;
import com.example.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SignService {
    @Autowired
    private SignRepository signRepository;
    @Autowired
    private SignMapper signMapper;
    @Autowired
    private AwsS3Service awsS3Service;
    @Autowired
    private UserRepository userRepository;

    public String createSign (Long userId, SignDto signDto, MultipartFile file){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String fileUrl = awsS3Service.uploadFile("sign",file);


    }

}
