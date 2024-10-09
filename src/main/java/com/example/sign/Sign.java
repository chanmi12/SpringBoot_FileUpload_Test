package com.example.sign;

import com.example.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "user_id", nullable = false)
     private User user;
     @Column(nullable = false)
     private String path;

    private boolean saved;
    private boolean deleted;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
        this.deleted=false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }

}
