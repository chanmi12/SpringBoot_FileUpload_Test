package com.example.work;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkRepository extends JpaRepository<Work, Long> {
    List<Work> findByUserId(Long userId);    // 주어진 userId를 가진 모든 Work 객체를 반환합니다.
    Optional<Work> findByIdAndUserId(Long id, Long userId); // 주어진 id와 userId를 가진 특정 Work 객체를 반환합니다.
    List<Work> findByUserIdAndTrashedTrue(Long userId); // 사용자 ID로 휴지통에 있는 작업 찾기
    List<Work> findByUserIdAndTrashedFalse(Long userId); // 사용자 ID로 휴지통에 없는 작업 찾기
    List<Work> findByUserIdAndSharedTrue(Long userId); // 사용자 ID로 공유된 작업 찾기

    @Query("SELECT w FROM Work w JOIN w.workItems wi WHERE wi.user.id = :userId AND w.trashed = false")
    List<Work> findWorksSharedWithUserNotTrashed(@Param("userId") Long userId);

}