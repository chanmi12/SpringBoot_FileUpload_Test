package com.example.sign;


import com.example.awsS3.AwsS3Service;
import com.example.user.User;
import com.example.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

//    public SignDto createSign(User user, MultipartFile file){
//        String fileUrl = awsS3Service.uploadFile("sign", file);
//
//        Sign sign = new Sign();
//        sign.setUser(user);
//        sign.setPath(fileUrl);
//        sign.setDeleted(false);
//        sign.setCreateDate(LocalDateTime.now());
//        sign.setUpdateDate(LocalDateTime.now());
//
//        Sign savedSign = signRepository.save(sign);
//
//        return signMapper.toDto(savedSign);
//}
//
//public List<SignDto> getAllSignForUser(User user){
//        List<Sign> signs = signRepository.findByUserIdAndDeletedFalse(user.getId());
//        return signs.stream()
//                .map(signMapper::toDto)
//                .collect(Collectors.toList());
//
//        public SignDto getSignForUser(User user, Long signId){
//            Optional<Sign> signOpt = signRepository.findByIdAndUserId(signId, user.getId());
//    }
//}

    public SignDto createSign(Long userId, MultipartFile file){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for given id"));

        String fileUrl = awsS3Service.uploadFile("sign", file);

        Sign sign = new Sign();
        sign.setUser(user);
        sign.setPath(fileUrl);
        sign.setDeleted(false);
        sign.setCreateDate(LocalDateTime.now());
        sign.setUpdateDate(LocalDateTime.now());

        Sign savedSign = signRepository.save(sign);

        return signMapper.toDto(savedSign);
    }

    public SignDto updateSign(Long userId, Long signId, MultipartFile file){
        Sign sign = signRepository.findByIdAndUserId(signId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Sign not found for given id and user"));
        String fileUrl = awsS3Service.uploadFile("sign", file);
        sign.setPath(fileUrl);
        sign.setUpdateDate(LocalDateTime.now());

        Sign updatedSign = signRepository.save(sign);

        return signMapper.toDto(updatedSign);
    }

    public List <SignDto> getSignsForUser(Long userId){
        List<Sign> signs = signRepository.findByUserIdAndDeletedFalse(userId);
        return signs.stream()
                .map(signMapper::toDto)
                .collect(Collectors.toList());
    }

    public SignDto getSignById(Long userId, Long signId){
        Sign sign = signRepository.findByIdAndUserId(signId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Sign not found for given id and user"));
        return signMapper.toDto(sign);
    }

    public void deleteSign(Long userId, Long signId){
        Sign sign = signRepository.findByIdAndUserId(signId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Sign not found for given id and user"));
        sign.setDeleted(true);
        sign.setUpdateDate(LocalDateTime.now());
        signRepository.save(sign);
    }
}
