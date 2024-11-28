package com.example.itext;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItextDto {
    private Long id;
    private Long workId;
    private String path;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
