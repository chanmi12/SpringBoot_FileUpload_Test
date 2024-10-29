package com.example.sign;

import com.example.workItem.WorkItemDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignRepository extends JpaRepository<Sign, Long> {
    // Find a Sign by its id and user id
    Optional<Sign> findByIdAndUserId(Long id, Long userId);
    //delete되지 않은 sign 찾기
    List<Sign> findByUserIdAndDeletedFalse(Long userId);
    //delete된 sign 찾기
    List<Sign> findByUserIdAndDeletedTrue(Long userId);

    Optional<Object> findById(WorkItemDto workItemDto);
}
