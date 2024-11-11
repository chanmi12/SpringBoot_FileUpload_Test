package com.example.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    List<User> findByNameContaining(String name); // Partial name search

    List<User> findByNameAndEmailContaining(String name, String email); // Filtered email search by name
    //학번으로 유저 조회
    Optional<User> findByUniqueId(String uniqueId);
    Optional<User> findByNameAndEmail(String name, String email);
}
