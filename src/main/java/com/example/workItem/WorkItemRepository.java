package com.example.workItem;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkItemRepository extends JpaRepository<WorkItem,Long> {
    // 특정 작업 ID에 대한 모든 작업 항목을 반환하는 쿼리
    @EntityGraph(attributePaths = {"sign"})
    List<WorkItem> findByWorkId(Long workId);
    // 특정 작업 ID 및 사용자 ID로 모든 작업 항목을 반환하는 쿼리
    @Query("SELECT wi FROM WorkItem wi WHERE wi.work.id = :workId AND wi.user.id = :otherId")
    List<WorkItem> findByWorkIdAndOtherUserId(@Param("workId") Long workId, @Param("otherId") Long otherId);
    // 특정 사용자 ID 및 작업 ID로 모든 작업 항목을 반환하는 쿼리
    List<WorkItem> findByUserIdAndWorkId(Long userId, Long workId);

    @EntityGraph(attributePaths = {"sign"})
    // 특정 작업 항목 ID로 작업 항목을 반환하는 쿼리
    Optional<WorkItem> findById(Long workItemId);
    @Query("SELECT DISTINCT wi.user.id FROM WorkItem wi WHERE wi.work.id = :workId")
    List<Long> findDistinctUserIdsByWorkId(@Param("workId") Long workId); // 특정 Work에 대한 모든 User ID를 반환하는 쿼리

    // AutoCreate가 false인 작업 ID 및 사용자 ID로 모든 작업 항목 찾기
    List<WorkItem> findByWorkIdAndUserIdAndAutoCreatedFalse(Long workId, Long userId);
    // AutoCreate가 true인 작업 ID 및 사용자 ID로 모든 작업 항목 찾기
    List<WorkItem> findByWorkIdAndUserIdAndAutoCreatedTrue(Long workId, Long userId);
   // 작업 ID로 작업 항목 수를 반환하는 쿼리
    @Query("SELECT COUNT(DISTINCT wi.user.id) FROM WorkItem wi WHERE wi.work.id = :workId")
    int countDistinctUsersByWorkId(@Param("workId") Long workId);

    List<WorkItem> findByWorkIdAndAutoCreatedFalse(Long workId);

    @Query("SELECT wi FROM WorkItem wi WHERE wi.work.id = :workId AND wi.autoCreated = false")
    List<WorkItem> findNonAutoCreatedWorkItemsByWorkId(@Param("workId") Long workId);


}
