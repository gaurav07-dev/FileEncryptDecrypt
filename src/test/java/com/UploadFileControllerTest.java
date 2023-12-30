package com;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UploadFileControllerTest {

    @Mock
    private EncryptDecrypt encryptDecrypt;

    @InjectMocks
    private UploadFileController uploadFileController;

    @Test
    public void testUploadPDFFile() throws IOException {
        byte[] pdfContent = "Sample PDF content".getBytes();
        MockMultipartFile pdfFile = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", pdfContent);

        // Mock behavior for encryption
        when(encryptDecrypt.encryptFile(pdfContent)).thenReturn("EncryptedPDF".getBytes());

        ResponseEntity<String> response = uploadFileController.uploadFile(pdfFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("File uploaded successfully: test.pdf.encrypted", response.getBody());
    }

    @Test
    public void testUploadImageFile() throws IOException {
        byte[] imageContent = "Sample image content".getBytes();
        MockMultipartFile imageFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", imageContent);

        // Mock behavior for encryption
        when(encryptDecrypt.encryptFile(imageContent)).thenReturn("EncryptedImage".getBytes());

        ResponseEntity<String> response = uploadFileController.uploadFile(imageFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("File uploaded successfully: test.jpg.encrypted", response.getBody());
    }

    @Test
    public void testInvalidFileType() throws IOException {
        byte[] invalidContent = "Invalid file content".getBytes();
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", invalidContent);

        ResponseEntity<String> response = uploadFileController.uploadFile(invalidFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Please select a valid file (only images and PDFs are allowed)", response.getBody());
    }
}
