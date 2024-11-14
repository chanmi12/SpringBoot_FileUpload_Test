package com.example.itext;

import com.example.work.WorkService;
import com.example.workItem.WorkItemService;
import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private WorkService workService;

    @Autowired
    private WorkItemService workItemService;

//    @GetMapping("/generatePdf/{workId}")  // 경로 /api/generatePdf/{workId}
//    public ResponseEntity<InputStreamResource> generatePdf(@PathVariable Long workId) {
//        try {
//            ByteArrayInputStream pdfStream = pdfService.generatePdf(workId);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-Disposition", "attachment; filename=generatedPdf.pdf");
//
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .contentType(MediaType.APPLICATION_PDF)
//                    .body(new InputStreamResource(pdfStream));
//
//        } catch (IOException | DocumentException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500)
//                    .contentType(MediaType.TEXT_PLAIN)
//                    .body(new InputStreamResource(new ByteArrayInputStream("PDF 생성 실패".getBytes())));
//        }
//    }
@GetMapping("/generatePdf/{workId}")
public ResponseEntity<InputStreamResource> generatePdf(@PathVariable Long workId) {
    try {
        ByteArrayInputStream pdfStream = pdfService.generatePdf(workId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=generated_pdf.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));

    } catch (IOException | com.lowagie.text.DocumentException e) {
        return ResponseEntity.status(500).build();
    }
}
}



