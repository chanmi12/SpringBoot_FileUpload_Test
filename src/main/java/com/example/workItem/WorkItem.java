package com.example.workItem;

import com.example.userWorkItem.UserWorkItem;
import com.example.work.entity.Work;
import com.example.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JoinColumn(name = "work_id", nullable = false)
    @JsonIgnore
    private Work work; // Work와의 관계 설정

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // New relationship with UserWorkItem
    @OneToMany(mappedBy = "workItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserWorkItem> userWorkItems; // List of UserWorkItems

    @Column(name = "sign_id", nullable = true)
    private Long signId = 0L;

    @Column(name = "type", nullable = true)
    private Integer type;

    @Column(name = "text", columnDefinition = "TEXT", nullable = true)
    private String text;

    @Column(name = "x_position", nullable = true)
    private Integer xPosition;

    @Column(name = "y_position", nullable = true)
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

    public WorkItem(Work work, User creator) {
    }


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
