package com.example.itext;

import com.example.work.WorkService;
import com.example.workItem.WorkItemService;
import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/itext")
@RequiredArgsConstructor
public class ItextController {

    @Autowired
    private ItextService itextService;

    @Autowired
    private WorkService workService;

    @Autowired
    private WorkItemService workItemService;
    @GetMapping("/download/{workId}")
    public ResponseEntity<ByteArrayResource> downloadItext(@PathVariable Long workId) {
        return itextService.createOrUpdateAndDownloadItext(workId);
    }
}
