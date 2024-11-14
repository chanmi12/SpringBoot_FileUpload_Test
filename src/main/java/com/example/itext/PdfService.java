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
        InputStream basePdfStream = awsS3Service.getFileAsStream(work.getPath());

        // Read the existing PDF
        PdfReader pdfReader = new PdfReader(basePdfStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper pdfStamper = new PdfStamper(pdfReader, baos);
        PdfContentByte content;

        // Set font for text, using Helvetica for clarity
        BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);

        // Get the dimensions of the PDF page
        float pdfWidth = pdfReader.getPageSize(1).getWidth();

        // Retrieve WorkItems and process each based on type
        List<WorkItem> workItems = workItemService.findWorksSharedWithUserNotTrashed(workId);
        for (WorkItem workItem : workItems) {
            content = pdfStamper.getOverContent(1);  // Adding to the first page

            // Calculate the flipped x-coordinate only, keeping y-coordinate as is
            float xPosition = pdfWidth - workItem.getXPosition();
            float yPosition = workItem.getYPosition(); // y-coordinate remains unchanged

            switch (workItem.getType()) {
                case 1: // Signature
                case 3:
                    if (workItem.getSign() != null) {
                        InputStream signImageStream = awsS3Service.getFileAsStream(workItem.getSign().getPath());
                        Image signImage = Image.getInstance(signImageStream.readAllBytes());

                        // Set the position and scale for high quality
                        signImage.setAbsolutePosition(xPosition, yPosition);
                        signImage.scaleToFit(100, 50); // Adjust size if needed
                        content.addImage(signImage);
                    }
                    break;

                case 2: // Text
                case 4:
                    content.beginText(); // Start text addition
                    content.setFontAndSize(baseFont, 12); // Set font and size for clarity
                    content.setTextMatrix(xPosition, yPosition); // Set text position
                    content.showText(workItem.getText()); // Show text from WorkItem
                    content.endText(); // End text addition
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported WorkItem type: " + workItem.getType());
            }
        }

        pdfStamper.close(); // Close the PdfStamper
        pdfReader.close(); // Close the PdfReader

        // Return the generated PDF as a ByteArrayInputStream
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
