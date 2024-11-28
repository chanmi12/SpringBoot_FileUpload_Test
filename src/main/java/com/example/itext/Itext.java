package com.example.itext;

import com.example.work.Work;
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
public class Itext {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @Column
    private String path;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    @PrePersist
    public void prePersist(){
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate(){
        this.updateDate = LocalDateTime.now();
    }
}


