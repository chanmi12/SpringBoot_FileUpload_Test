package com.example.work.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Work")
@Getter
@Setter
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String name;
    private String path; // S3 path

    private Integer xSize;
    private Integer ySize;

    @Column(columnDefinition = "boolean default false")
    private Boolean shared = false;

    @Column(columnDefinition = "boolean default false")
    private Boolean trashed = false;

    @Column(columnDefinition = "boolean default false")
    private Boolean finish = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateDate;

    private LocalDateTime deleteDate;
    private LocalDateTime openDate;
}