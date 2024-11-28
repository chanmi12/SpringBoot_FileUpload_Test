package com.example.itext;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.awsS3.AwsS3Service;
import com.example.work.Work;
import com.example.work.WorkRepository;
import com.example.work.WorkService;
import com.example.workItem.WorkItem;
import com.example.workItem.WorkItemRepository;
import com.example.workItem.WorkItemService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItextService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final AwsS3Service awsS3Service;
    private final WorkRepository workRepository;
    private final WorkItemRepository workItemRepository;
    private final ItextRepository itextRepository;

    /**
     * WorkItems를 기반으로 PDF 파일 생성 후 다운로드
     */
    @Transactional
    public ResponseEntity<ByteArrayResource> createOrUpdateAndDownloadItext(Long workId) {
        // Fetch the Work entity
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new IllegalArgumentException("Work not found with ID: " + workId));
        List<WorkItem> workItems = workItemRepository.findByWorkIdAndFinishedTrueAndAutoCreatedFalse(workId);

        if (workItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No valid WorkItems found.");
        }

        String workPath = work.getPath(); // Path to the base Work PDF
        if (workPath == null || workPath.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Work PDF path is missing.");
        }

        Itext itext = work.getItext();
        String s3Path;

        if (itext == null) {
            // Create new Itext
            ByteArrayOutputStream pdfOutputStream = generatePdf(workItems, workPath);
            s3Path = uploadPdfToS3(workId, pdfOutputStream);

            itext = new Itext();
            itext.setPath(s3Path);
            itext.setWork(work);
            itext = itextRepository.save(itext);

            work.setItext(itext);
            workRepository.save(work);
        } else {
            // Update existing Itext
            ByteArrayOutputStream pdfOutputStream = generatePdf(workItems, workPath);
            s3Path = uploadPdfToS3(workId, pdfOutputStream);

            itext.setPath(s3Path);
            itextRepository.save(itext);
        }

        // Use Work.name from the database for the file name
        String fileName = work.getName() + ".pdf";

        // Return the file with the custom name
        return downloadPdfFromS3(s3Path, fileName);
    }

    private ByteArrayOutputStream generatePdf(List<WorkItem> workItems, String workPath) {
        try {
            // Step 1: Retrieve base Work PDF
            InputStream basePdfStream = awsS3Service.getFileAsStream(workPath);
            PdfReader pdfReader = new PdfReader(basePdfStream);

            // Step 2: Create PDF output stream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);

            // Step 3: Prepare fonts for text
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);

            // Step 4: Iterate over WorkItems and add to PDF
            PdfContentByte content = pdfStamper.getOverContent(1);
            for (WorkItem workItem : workItems) {
                switch (workItem.getType()) {
                    case 1: // Signature
                    case 3:
                        if (workItem.getSign() != null) {
                            InputStream signImageStream = awsS3Service.getFileAsStream(workItem.getSign().getPath());
                            Image signImage = Image.getInstance(signImageStream.readAllBytes());
                            signImage.setAbsolutePosition(workItem.getXPosition(), workItem.getYPosition());
                            signImage.scaleToFit(100, 50); // Adjust size as needed
                            content.addImage(signImage);
                        }
                        break;

                    case 2: // Text
                    case 4:
                        content.beginText();
                        content.setFontAndSize(baseFont, 12);
                        content.setTextMatrix(workItem.getXPosition(), workItem.getYPosition());
                        content.showText(workItem.getText());
                        content.endText();
                        break;

                    default:
                        throw new IllegalArgumentException("Unsupported WorkItem type: " + workItem.getType());
                }
            }

            // Step 5: Close the PDF stamper and reader
            pdfStamper.close();
            pdfReader.close();
            return outputStream;

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate PDF", e);
        }
    }

    /**
     * 생성된 PDF 파일을 AWS S3에 업로드하고 URL 반환
     */
    @Transactional
    public String uploadPdfToS3(Long workId, ByteArrayOutputStream pdfOutputStream) {
        String fileName = "itext/" + workId + "_work.pdf"; // 파일 경로 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/pdf");
        metadata.setContentLength(pdfOutputStream.size()); // 파일 크기 설정

        try (InputStream pdfInputStream = new ByteArrayInputStream(pdfOutputStream.toByteArray())) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, pdfInputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)); // S3에 업로드
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드 실패.");
        }
        return amazonS3.getUrl(bucket, fileName).toString(); // 업로드된 파일의 URL 반환
    }

    /**
     * S3에서 PDF 파일 다운로드
     */
    @Transactional
    public ResponseEntity<ByteArrayResource> downloadPdfFromS3(String s3Path, String originalFileName) {
        try {
            // AWS S3에서 파일 가져오기
            InputStream inputStream = awsS3Service.getFileAsStream(s3Path);
            ByteArrayResource resource = new ByteArrayResource(inputStream.readAllBytes());

            // 파일 이름 URL 인코딩
            String encodedFileName = UriUtils.encode(originalFileName, StandardCharsets.UTF_8);

            // UTF-8로 인코딩된 파일 이름을 Content-Disposition 헤더에 추가
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to download PDF", e);
        }
    }
}
