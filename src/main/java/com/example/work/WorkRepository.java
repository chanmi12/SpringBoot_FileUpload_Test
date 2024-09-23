package com.example.work;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkRepository extends JpaRepository<Work , Long> {
    List<Work> findByUserId(Long userId);
}
