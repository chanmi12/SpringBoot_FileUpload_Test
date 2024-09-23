package com.example.workfileuploadtest.repository;

import com.example.workfileuploadtest.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkRepository extends JpaRepository<Work, Long> {
    List<Work> findByUserId(Long userId);

    Optional<Work> findByIdAndUserId(Long id, Long userId);
}