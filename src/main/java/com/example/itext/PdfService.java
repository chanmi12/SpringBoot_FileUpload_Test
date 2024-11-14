package com.example.itext;

import com.example.awsS3.AwsS3Service;
import com.example.sign.SignService;
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
import com.lowagie.text.Rectangle;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.*;
import java.net.URL;
import java.util.List;

@Service
public class PdfService {

    @Autowired
    private AwsS3Service awsS3Service;

    @Autowired
    private WorkService workService;

    @Autowired
    private WorkItemService workItemService;

    @Autowired
    private SignService signService;
    //픽셀을 포인트로 변환하는 상수
    private static final float PIXEL_TO_POINT_CONVERSION = 0.75f;
//    @Transactional
//    public ByteArrayInputStream generatePdf(Long workId) throws IOException, DocumentException {//PDF 생성 메소드
//        // Work 정보를 불러와 S3에서 기존 PDF 파일을 로드
//        Work work = workService.getWorkById(workId);
//        InputStream basePdfStream = awsS3Service.getFileAsStream(work.getPath());
//
//        // 기존 PDF를 PdfReader로 읽어옴
//        PdfReader pdfReader = new PdfReader(basePdfStream);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        // 페이지 크기 조정 예시: 여기서 원하는 크기를 Rectangle로 정의
//        Rectangle pageSize = new Rectangle(PageSize.A4); // A4 사이즈 예시
//        Document document = new Document(pageSize);
//        PdfWriter writer = PdfWriter.getInstance(document, baos);
//
//        // 새 문서 시작
//        document.open();
//
//        // PDF 콘텐츠를 추가하기 위해 PdfStamper 사용
//        PdfStamper pdfStamper = new PdfStamper(pdfReader, baos);
//        PdfContentByte content;
//
//        // 기본 폰트 설정 (Helvetica, ANSI 인코딩, 폰트 포함)
//        BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
//
//        // WorkItem을 불러와 각 타입에 따라 PDF에 추가
//        List<WorkItem> workItems = workItemService.findWorksSharedWithUserNotTrashed(workId);
//        for (WorkItem workItem : workItems) {
//            content = pdfStamper.getOverContent(1);  // 첫 번째 페이지에 추가
//
//            // WorkItem의 타입에 따라 서명 이미지 또는 텍스트 추가
//            switch (workItem.getType()) {
//                case 1: // 서명
//                case 3:
//                    if (workItem.getSign() != null) {
//                        // 서명 이미지 경로를 S3에서 불러옴
//                        InputStream signImageStream = awsS3Service.getFileAsStream(workItem.getSign().getPath());
//                        Image signImage = Image.getInstance(signImageStream.readAllBytes());
//
//                        // 서명 이미지 위치 및 크기 설정
//                        signImage.setAbsolutePosition(workItem.getXPosition(), workItem.getYPosition());
//                        signImage.scaleToFit(100, 50);
//                        content.addImage(signImage);
//                    }
//                    break;
//
//                case 2: // 텍스트
//                case 4:
//                    content.beginText(); // 텍스트 추가 시작
//                    content.setFontAndSize(baseFont, 12); // 폰트 및 크기 설정
//                    content.setTextMatrix(workItem.getXPosition(), workItem.getYPosition()); // 텍스트 위치 설정
//                    content.showText(workItem.getText()); // 텍스트 내용
//                    content.endText(); // 텍스트 추가 종료
//                    break;
//
//                default:
//                    throw new IllegalArgumentException("지원되지 않는 WorkItem 타입: " + workItem.getType());
//            }
//        }
//
//        pdfStamper.close(); // PdfStamper 닫기
//        pdfReader.close(); // PdfReader 닫기
//
//        // 최종 PDF 데이터 반환
//        return new ByteArrayInputStream(baos.toByteArray());
//    }
//}
@Transactional
public ByteArrayInputStream generatePdf(Long workId) throws IOException, DocumentException {
    // Load the base PDF from S3 using the Work path
    Work work = workService.getWorkById(workId);
    InputStream basePdfStream = awsS3Service.getFileAsStream(work.getPath());

    PdfReader pdfReader = new PdfReader(basePdfStream);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PdfStamper pdfStamper = new PdfStamper(pdfReader, baos);
    PdfContentByte content;

    // Set font for text
    BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);

    // Get page dimensions in points
    float pageWidthInPoints = PageSize.A4.getWidth();
    float pageHeightInPoints = PageSize.A4.getHeight();

    // Retrieve WorkItems and process each based on type
    List<WorkItem> workItems = workItemService.findWorksSharedWithUserNotTrashed(workId);
    for (WorkItem workItem : workItems) {
        content = pdfStamper.getOverContent(1);  // Adding to first page for this example

        // Adjust x and y positions from pixels to points and flip x-axis if necessary
        float xPositionInPoints = (pageWidthInPoints - workItem.getXPosition() * PIXEL_TO_POINT_CONVERSION);
        float yPositionInPoints = workItem.getYPosition() * PIXEL_TO_POINT_CONVERSION;

        switch (workItem.getType()) {
            case 1: // Signature
            case 3:
                if (workItem.getSign() != null) {
                    InputStream signImageStream = awsS3Service.getFileAsStream(workItem.getSign().getPath());
                    Image signImage = Image.getInstance(signImageStream.readAllBytes());
                    signImage.setAbsolutePosition(xPositionInPoints, yPositionInPoints);
                    signImage.scaleToFit(100 * PIXEL_TO_POINT_CONVERSION, 50 * PIXEL_TO_POINT_CONVERSION); // Scale based on pixels
                    content.addImage(signImage);
                }
                break;

            case 2: // Text
            case 4:
                content.beginText();
                content.setFontAndSize(baseFont, 12);
                content.setTextMatrix(xPositionInPoints, yPositionInPoints);
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