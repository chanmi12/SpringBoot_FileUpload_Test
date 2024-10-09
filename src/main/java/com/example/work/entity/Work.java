package com.example.work.entity;

import com.example.userWorkItem.UserWorkItem;
import com.example.workItem.WorkItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

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


    private Boolean shared;

    private Boolean trashed;

    private Boolean finish;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateDate;

    private LocalDateTime deleteDate;
    private LocalDateTime openDate;

    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkItem> workItems;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
        this.shared = false;
        this.trashed = false;
        this.finish = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();

    }
}