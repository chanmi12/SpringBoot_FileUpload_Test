package com.example.work;

import com.example.itext.ItextDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkDto {

    private Long id;
    private Long userId;
    private String name;
    private String path;
    private Integer xSize;
    private Integer ySize;
    private Boolean shared;
    private Boolean trashed;
    private Boolean finish;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private LocalDateTime openDate;
    private LocalDateTime deleteDate;
    private Integer userCount;
    private ItextDto itext;
}