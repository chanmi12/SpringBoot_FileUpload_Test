package com.example.user;

//import com.example.auth.dto.AuthDto;
import com.example.auth.dto.AuthDto;
import com.example.sign.Sign;
import com.example.userWorkItem.UserWorkItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_id", nullable = false, length = 50)
    private String uniqueId;


    private String name;

    @Column(name = "email" )
    private String email;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "semester")
    private Integer semester;

    @Column(name = "department", length = 50)
    private String department;

    @Column(name = "major1", length = 50)
    private String major1;

    @Column(name = "major2", length = 50)
    private String major2;

    @Column(length = 255, nullable=false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private LocalDateTime updateDate;
    private LocalDateTime loginTime;
    private Integer level;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserWorkItem> userWorkItems; // Link to UserWorkItem

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
   @JsonIgnore
    private List<Sign> signs; // User가 여러 개의 Sign을 가질 수 있음

    public User(AuthDto authDto) {
        this.uniqueId = authDto.getUniqueId();
        this.name = authDto.getName();
        this.email = authDto.getEmail();
        this.grade = authDto.getGrade();
        this.semester = authDto.getSemester();
        this.department = authDto.getDepartment();
        this.major1 = authDto.getMajor1();
        this.major2 = authDto.getMajor2();
    }

    public void update(AuthDto dto) {
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.grade = dto.getGrade();
        this.semester = dto.getSemester();
        this.department = dto.getDepartment();
        this.major1 = dto.getMajor1();
        this.major2 = dto.getMajor2();
    }
    public static User from(AuthDto dto) {
        return User.builder()
                .uniqueId(dto.getUniqueId())
                .name(dto.getName())
                .email(dto.getEmail())
                .grade(dto.getGrade())
                .semester(dto.getSemester())
                .department(dto.getDepartment())
                .major1(dto.getMajor1())
                .major2(dto.getMajor2())
                .build();
    }

    public void updateLoginTime() {
        this.loginTime = LocalDateTime.now();
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
