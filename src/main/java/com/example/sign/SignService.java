package com.example.sign;


import com.example.awsS3.AwsS3Service;
import com.example.user.User;
import com.example.user.UserRepository;
import jakarta.transaction.Transactional;
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
    private UserRepository userRepository;
    @Autowired
    private SignMapper signMapper;
    @Autowired
    private AwsS3Service awsS3Service;



//    public SignDto createSign(Long userId, MultipartFile file){
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found for given id"));
//
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
//    }

    public SignDto createSign (Long userId, MultipartFile file){
        User user = userRepository.findById(userId)
                .orElseThrow(() ->  new IllegalArgumentException("User not found for given id" + userId));

        String fileUrl = awsS3Service.uploadFile("sign", file);

        Sign sign = new Sign();
        sign.setUser(user); //User 세팅
        sign.setPath(fileUrl);
        sign.setDeleted(false);
        sign.setCreateDate(LocalDateTime.now());
        sign.setUpdateDate(LocalDateTime.now());

        Sign savedSign = signRepository.save(sign);
        SignDto signDto = signMapper.toDto(savedSign);
        signDto.setUserId(user.getId());

        return signDto;
    }
//    @Transactional
//    public SignDto updateSign(Long userId, Long signId, MultipartFile file){
//        Sign sign = signRepository.findByIdAndUserId(signId, userId)
//                .orElseThrow(() -> new IllegalArgumentException("Sign not found for given id and user"));
//        String fileUrl = awsS3Service.uploadFile("sign", file);
//        sign.setPath(fileUrl);
//        sign.setUpdateDate(LocalDateTime.now());
//
//        Sign updatedSign = signRepository.save(sign);
//
//        return signMapper.toDto(updatedSign);
//    }

    public String updateSignFile(Long signId, Long userId, MultipartFile file){
        Optional <Sign> signOpt = signRepository.findByIdAndUserId(signId, userId);
        if(!signOpt.isPresent()){
            throw new IllegalArgumentException("Sign not found for given id and user");
        }
        Sign existingSign = signOpt.get();

        awsS3Service.deleteFileFromS3(existingSign.getPath());

        String newFileUrl = awsS3Service.uploadFile("sign", file);
        existingSign.setPath(newFileUrl);
        existingSign.setUpdateDate(LocalDateTime.now());

        signRepository.save(existingSign);

        return newFileUrl;
    }

    @Transactional
    public SignDto updateSignFields(Long signId, Long userId, SignDto signDto){
        Optional <Sign> signOpt = signRepository.findByIdAndUserId(signId, userId);
        if(!signOpt.isPresent()){
            throw new IllegalArgumentException("Sign not found for given id and user");
        }
        Sign existingSign = signOpt.get();

        if (signDto.getPath() != null) {
            existingSign.setPath(signDto.getPath());
        }

        existingSign.setSaved(signDto.isSaved());
        existingSign.setDeleted(signDto.isDeleted());

        if (signDto.getCreateDate() != null) {
            existingSign.setCreateDate(signDto.getCreateDate());
        }
        if (signDto.getUpdateDate() != null) {
            existingSign.setUpdateDate(signDto.getUpdateDate());
        }
        signRepository.save(existingSign);

        return signMapper.toDto(existingSign);
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
