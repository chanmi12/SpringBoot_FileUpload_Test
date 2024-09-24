package com.example.workItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkItemRepository extends JpaRepository<WorkItem,Long> {
    List<WorkItem> findByWorkId(Long workId);
}