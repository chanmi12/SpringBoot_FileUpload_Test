package com.example.workItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.user.User;
import com.example.work.entity.Work;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkItemRepository extends JpaRepository<WorkItem,Long> {
    List<WorkItem> findByWorkId(Long workId);
}