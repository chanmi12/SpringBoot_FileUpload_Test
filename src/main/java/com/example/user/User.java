package com.example.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable=false)
    private String name;
    @Column(length = 255, nullable=false , unique=true)
    private String email;
    @Column(length = 255, nullable=false)
    private LocalDateTime createDate = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updateDate = LocalDateTime.now();
    private LocalDateTime localDateTime;
    private Integer level;
}
