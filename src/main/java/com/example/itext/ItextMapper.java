package com.example.itext;

import com.example.work.Work;
import org.springframework.stereotype.Component;


@Component
public class ItextMapper {

    // Map Itext entity to ItextDto
    public ItextDto toDto(Itext itext) {
        if (itext == null) {
            return null;
        }
        return new ItextDto(
                itext.getId(),
                itext.getWork().getId(), // Ensures the associated Work ID is included
                itext.getPath(),
                itext.getCreateDate(),
                itext.getUpdateDate()
        );
    }

    // Map ItextDto to Itext entity
    public Itext toEntity(ItextDto itextDto, Work work) {
        if (itextDto == null || work == null) {
            return null;
        }
        Itext itext = new Itext();
        itext.setId(itextDto.getId());
        itext.setWork(work); // Establishes the OneToOne relationship with Work
        itext.setPath(itextDto.getPath());
        itext.setCreateDate(itextDto.getCreateDate());
        itext.setUpdateDate(itextDto.getUpdateDate());
        return itext;
    }

    // Update existing Itext entity from ItextDto
    public void updateEntityFromDto(ItextDto itextDto, Itext existingItext) {
        if (itextDto == null || existingItext == null) {
            return;
        }
        if (itextDto.getPath() != null) {
            existingItext.setPath(itextDto.getPath());
        }
        if (itextDto.getCreateDate() != null) {
            existingItext.setCreateDate(itextDto.getCreateDate());
        }
        if (itextDto.getUpdateDate() != null) {
            existingItext.setUpdateDate(itextDto.getUpdateDate());
        }
    }
}
