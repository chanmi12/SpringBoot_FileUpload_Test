package com.example.sign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignRepository extends JpaRepository<Sign, Long> {
    List<Sign> finByUserIdAndDeletedFalse(Long userId); //유저의 Sign 목록 조회
    Optional<Sign> findByIdAndUserIdAndDeletedFalse(Long id , Long userId); //유저의 Sign 조회
}
