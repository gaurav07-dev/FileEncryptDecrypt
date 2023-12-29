package com;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UploadFileControllerTest {

    @InjectMocks
    private UploadFileController uploadFileController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testImageAndPDFUpload() {
        // Attempt to upload an image file (PNG)
        MultipartFile pngFile = new MockMultipartFile("file", "image.png", MediaType.IMAGE_PNG_VALUE, "Some content".getBytes());
        String pngUploadResponse = uploadFileController.uploadFile(pngFile).getBody();
        assertEquals("File uploaded successfully: image.png", pngUploadResponse);

        // Attempt to upload an image file (JPEG)
        MultipartFile jpegFile = new MockMultipartFile("file", "image.jpeg", MediaType.IMAGE_JPEG_VALUE, "Some content".getBytes());
        String jpegUploadResponse = uploadFileController.uploadFile(jpegFile).getBody();
        assertEquals("File uploaded successfully: image.jpeg", jpegUploadResponse);

        // Attempt to upload a PDF file
        MultipartFile pdfFile = new MockMultipartFile("file", "document.pdf", MediaType.APPLICATION_PDF_VALUE, "Some content".getBytes());
        String pdfUploadResponse = uploadFileController.uploadFile(pdfFile).getBody();
        assertEquals("File uploaded successfully: document.pdf", pdfUploadResponse);
    }

    @Test
    public void testNonImageOrPDFUpload() {
        // Attempt to upload a non-image and non-PDF file (TXT file)
        MultipartFile txtFile = new MockMultipartFile("file", "text.txt", MediaType.TEXT_PLAIN_VALUE, "Some content".getBytes());
        String txtUploadResponse = uploadFileController.uploadFile(txtFile).getBody();
        assertEquals("Please select a valid file (only images and PDFs are allowed)", txtUploadResponse);
    }
}
