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

    @Transactional
    public Long createSign (Long userId, MultipartFile file, boolean deleted){
        User user = userRepository.findById(userId)
                .orElseThrow(() ->  new IllegalArgumentException("User not found for given id" + userId));

        String fileUrl = awsS3Service.uploadFile("sign", file);

        Sign sign = new Sign();
        sign.setUser(user); //User 세팅
        sign.setPath(fileUrl);
        sign.setDeleted(deleted);
        sign.setCreateDate(LocalDateTime.now());
        sign.setUpdateDate(LocalDateTime.now());

        Sign savedSign = signRepository.save(sign);

        return savedSign.getId();
    }

    @Transactional
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


    //휴지통 이동
    @Transactional
    public SignDto  moveToTrash(Long signId){
        Sign sign = signRepository.findById(signId)
                .orElseThrow(() -> new IllegalArgumentException("Sign not found for given id"));
        sign.setDeleted(true);

        Sign updatedSign = signRepository.save(sign);
        return signMapper.toDto(updatedSign);
    }

    //휴지통에서 복구
    @Transactional
    public SignDto restoreFromTrash(Long signId){
        Sign sign = signRepository.findById(signId)
                .orElseThrow(() -> new IllegalArgumentException("Sign not found for given id" + signId));

        sign.setDeleted(false);
        Sign updatedSign = signRepository.save(sign);
        return signMapper.toDto(updatedSign);
    }
    //휴지통에 있는 sign 가져오기
    public List<SignDto> getDeletedSignsForUser(Long userId){
        List<Sign> deletedSigns = signRepository.findByUserIdAndDeletedTrue(userId);
        return deletedSigns.stream()
                .map(signMapper::toDto)
                .collect(Collectors.toList());
    }
    //delete되지 않은 sign 가져오기
    public List<SignDto> getNonDeletedSignsForUser(Long userId){
        List<Sign> nonDeletedSign = signRepository.findByUserIdAndDeletedFalse(userId);
        return nonDeletedSign.stream()
                .map(signMapper::toDto)
                .collect(Collectors.toList());
    }
}
