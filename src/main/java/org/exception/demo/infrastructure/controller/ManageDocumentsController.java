package org.exception.demo.infrastructure.controller;

import java.util.List;

import org.exception.demo.application.service.ManageDocumentService;
import org.exception.demo.application.service.bean.DocumentData;
import org.exception.demo.application.service.exception.DocumentNotFoundException;
import org.exception.demo.application.service.exception.DocumentTypeNotFoundException;
import org.exception.demo.application.service.exception.DownloadFileException;
import org.exception.demo.application.service.exception.FileExtensionException;
import org.exception.demo.application.service.exception.UploadFileException;
import org.exception.demo.domain.Document;
import org.exception.demo.domain.DocumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/document")
public class ManageDocumentsController {

	private static final Logger logger = LoggerFactory.getLogger(ManageDocumentsController.class);

	@Autowired
	private ManageDocumentService manageDocumentService;

	@GetMapping("/user/{userId}")
	ResponseEntity<Page<Document>> documentsByUser(@PathVariable Integer userId,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size,
			@RequestParam(defaultValue = "asc") String order) {
		try {

			logger.info("documentsByUser userId: {} page: {} size: {}  order: {}", userId, page, size, order);
			Page<Document> documents = manageDocumentService.getDocuments(userId, page, size, order);

			return new ResponseEntity<Page<Document>>(documents, HttpStatus.OK);

		} catch (Exception ex) {
			logger.error(ex.getMessage());
			// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
		} finally {
			logger.info("documentsByUser");
		}
	}

	@GetMapping("/type")
	ResponseEntity<List<DocumentType>> documentTypes() {
		try {
			logger.info("documentTypes");
			List<DocumentType> documentType = manageDocumentService.getDocumentTypes();
			return new ResponseEntity<List<DocumentType>>(documentType, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
		} finally {
			logger.info("documentTypes");
		}
	}

	@PostMapping("/upload/{userId}")
	public ResponseEntity<Document> fileUpload(@PathVariable Integer userId, @RequestParam("file") MultipartFile file,
			@RequestParam Long type) {
		try {
			logger.info("fileUpload userId: {} type: {}", userId, type);
			Document document = manageDocumentService.saveDocument(userId, file, type);
			return new ResponseEntity<Document>(document, HttpStatus.OK);
		} catch (DocumentTypeNotFoundException ex) {
			logger.error(ex.getMessage());
			// return ResponseEntity.status(HttpStatus.BAD_REQUEST);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		} catch (FileExtensionException ex) {
			logger.error(ex.getMessage());
			// return ResponseEntity.status(HttpStatus.BAD_REQUEST);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		} catch (UploadFileException ex) {
			logger.error(ex.getMessage());
			// return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE);
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), ex);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
		} finally {
			logger.info("fileUpload");
		}
	}

	@GetMapping("/download/{userId}/file/{fileId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable Integer userId, @PathVariable Long fileId) {

		try {
			logger.info("downloadFile userId: {} fileId: {}", userId, fileId);
			DocumentData documentData = manageDocumentService.getDocument(userId, fileId);

			HttpHeaders header = new HttpHeaders();
			header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + documentData.getFilename());

			ByteArrayResource resource = new ByteArrayResource(documentData.getContent());

			return ResponseEntity.ok().headers(header).contentLength(documentData.getContent().length)
					.contentType(MediaType.parseMediaType(documentData.getContentType())).body(resource);

		} catch (DocumentNotFoundException ex) {
			logger.error(ex.getMessage());
			// return ResponseEntity.status(HttpStatus.BAD_REQUEST);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		} catch (DownloadFileException ex) {
			logger.error(ex.getMessage());
			// return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE);
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), ex);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
		} finally {
			logger.info("downloadFile");
		}
	}

}
