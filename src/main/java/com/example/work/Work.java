package com.example.work;

import com.example.itext.Itext;
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
    @Column(length = 1024)
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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
    @JoinColumn(name = "itext_id", referencedColumnName = "id", nullable = true)
    private Itext itext;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
        this.openDate = LocalDateTime.now();
        this.shared = false;
        this.trashed = false;
        this.finish = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}