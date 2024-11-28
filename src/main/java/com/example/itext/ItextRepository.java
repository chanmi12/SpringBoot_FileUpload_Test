package com.example.itext;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItextRepository extends JpaRepository<Itext,Long> {
    Optional<Itext> findByWorkId(Long workId);
}
