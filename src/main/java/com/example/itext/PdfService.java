package com.example.itext;

import com.example.awsS3.AwsS3Service;
import com.example.work.Work;
import com.example.work.WorkService;
import com.example.workItem.WorkItem;
import com.example.workItem.WorkItemService;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class PdfService {

    @Autowired
    private AwsS3Service awsS3Service;

    @Autowired
    private WorkService workService;

    @Autowired
    private WorkItemService workItemService;

    @Transactional
    public ByteArrayInputStream generatePdf(Long workId) throws IOException, DocumentException {
        // Load the base PDF from S3 using the Work path
        Work work = workService.getWorkById(workId);
        String workPath = work.getPath();
        InputStream basePdfStream = awsS3Service.getFileAsStream(workPath);

        PdfReader pdfReader = new PdfReader(basePdfStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper pdfStamper = new PdfStamper(pdfReader, baos);
        PdfContentByte content;

        // Set font for text
        BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);

        // Retrieve WorkItems and process each based on type
        List<WorkItem> workItems = workItemService.findWorksSharedWithUserNotTrashed(workId);
        for (WorkItem workItem : workItems) {
            content = pdfStamper.getOverContent(1);

            float xPosition = workItem.getXPosition();
            float yPosition = workItem.getYPosition();

            switch (workItem.getType()) {
                case 1: // Signature
                case 3:
                    if (workItem.getSign() != null) {
                        String signPath = workItem.getSign().getPath();
                        InputStream signImageStream = awsS3Service.getFileAsStream(signPath);
                        Image signImage = Image.getInstance(signImageStream.readAllBytes());

                        // Set image position and size
                        signImage.setAbsolutePosition(xPosition, yPosition);
                        signImage.scaleToFit(100, 50);
                        content.addImage(signImage);
                    }
                    break;

                case 2: // Text
                case 4:
                    content.beginText();
                    content.setFontAndSize(baseFont, 12);
                    content.setTextMatrix(xPosition, yPosition);
                    content.showText(workItem.getText());
                    content.endText();
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported WorkItem type: " + workItem.getType());
            }
        }

        pdfStamper.close();
        pdfReader.close();

        return new ByteArrayInputStream(baos.toByteArray());
    }
}
