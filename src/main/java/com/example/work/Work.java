package com.example.work;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="work")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private Long userId;
    private String name;
    private String path;
    private Integer xSize;
    private Integer ySize;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean shared = false;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean trashed = false;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean finish = false;

    @Column(nullable = false)
    private LocalDateTime createDate = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updateDate = LocalDateTime.now();
    private LocalDateTime deleteDate;
    private LocalDateTime openDate;
}
