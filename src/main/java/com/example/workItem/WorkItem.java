package com.example.workItem;

import com.example.userWorkItem.UserWorkItem;
import com.example.work.entity.Work;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workId", nullable = false)
    private Work work; // Work와의 관계 설정

    // New relationship with UserWorkItem
    @OneToMany(mappedBy = "workItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserWorkItem> userWorkItems; // List of UserWorkItems

    @Column(name = "signId", nullable = false)
    private Long signId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "text", columnDefinition = "TEXT", nullable = true)
    private String text;

    @Column(name = "xPosition", nullable = true)
    private Integer xPosition;

    @Column(name = "yPosition", nullable = true)
    private Integer yPosition;

    @Column(name = "width", nullable = true)
    private Integer width;

    @Column(name = "height", nullable = true)
    private Integer height;

    @Column(name = "createDate", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Column(name = "updateDate", nullable = false)
    private LocalDateTime updateDate;

    @Column(name = "free", nullable = true)
    private Boolean free;

    @Column(name = "page", nullable = true)
    private Integer page;

    @Column(name = "fontSize", nullable = true)
    private Integer fontSize;

    @Column(name = "fontStyle", length = 255, nullable = true)
    private String fontStyle;


    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}
