package com.example.work;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkRepository extends JpaRepository<Work, Long> {
    List<Work> findByUserId(Long userId);
    Optional<Work> findByIdAndUserId(Long id, Long userId);
    List<Work> findByUserIdAndTrashedTrue(Long userId);
    List<Work> findByUserIdAndTrashedFalse(Long userId);
    List<Work> findByUserIdAndSharedTrue(Long userId);
}