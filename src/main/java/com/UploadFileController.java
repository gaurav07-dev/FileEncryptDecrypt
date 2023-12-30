package com;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
public class UploadFileController {

	EncryptDecrypt encryptDecrypt;

	public UploadFileController(EncryptDecrypt encryptDecrypt) {
		this.encryptDecrypt = encryptDecrypt;
	}

	private static final String UPLOAD_DOWNLOAD_DIR = "/home/gaurav/Documents/UploadedFileStorage";
	private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png",
			"application/pdf");

	private static final String FILE_TO_BE_DOWNLOADED = "Web Tech.zip.encrypted";

	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile originalFile) {
		if (originalFile.isEmpty()) {
			return ResponseEntity.badRequest().body("Request received without any file");
		}

		String contentType = originalFile.getContentType();
		if (contentType != null && !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
			return ResponseEntity.badRequest().body("Please select a valid file (only images and PDFs are allowed)");
		}

		try {
			byte[] encrptedFile = encryptDecrypt.encryptFile(originalFile.getBytes());
			String fileName = originalFile.getOriginalFilename() + ".encrypted";
			Path path = Paths.get(UPLOAD_DOWNLOAD_DIR, fileName);
			Files.write(path, encrptedFile);
			return ResponseEntity.ok("File uploaded successfully: " + fileName);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload file: " + e.getMessage());
		}
	}

	@GetMapping("/download")
	public ResponseEntity<Resource> downloadFile() throws IOException {
		String fileName = FILE_TO_BE_DOWNLOADED;
		Path filePath = Paths.get(UPLOAD_DOWNLOAD_DIR, fileName);
		byte[] encryptedData = Files.readAllBytes(filePath);
		byte[] decryptedData = encryptDecrypt.decryptFile(encryptedData);

		Path newFilePath = Paths.get(UPLOAD_DOWNLOAD_DIR, fileName.substring(0, fileName.lastIndexOf(".encrypted")));
		Files.write(newFilePath, decryptedData);
		var fileResource = new FileSystemResource(newFilePath.toFile());

		if (fileResource.exists() && fileResource.isReadable()) {
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
					.contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileResource);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
