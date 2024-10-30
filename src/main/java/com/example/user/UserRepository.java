package com.example.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUniqueId(String uniqueId);
    List<User> findByNameContaining(String name); // Partial name search

    List<User> findByNameAndEmailContaining(String name, String email); // Filtered email search by name
}
