package com.example.workItem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkItemDto {
    private Long id;
    private Long workId;
    private Long signId;
    private Long userId;
    private Integer type;
    private String text;
    private Integer xPosition;
    private Integer yPosition;
    private Integer width;
    private Integer height;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private Boolean free;
    private Integer page;
    private Integer fontSize;
    private String fontStyle;
}
