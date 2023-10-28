package org.exception.demo.application.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.exception.demo.application.repository.DocumentRepository;
import org.exception.demo.application.repository.DocumentTypeRepository;
import org.exception.demo.application.service.bean.DocumentData;
import org.exception.demo.application.service.exception.DocumentNotFoundException;
import org.exception.demo.application.service.exception.DocumentTypeNotFoundException;
import org.exception.demo.application.service.exception.DownloadFileException;
import org.exception.demo.application.service.exception.FileExtensionException;
import org.exception.demo.application.service.exception.UploadFileException;
import org.exception.demo.domain.Document;
import org.exception.demo.domain.DocumentType;
import org.exception.demo.infrastructure.file.DiskFile;
import org.exception.demo.infrastructure.file.FileManagementInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ManageDocumentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ManageDocumentService.class);

	private static final String REPOSITORY = "./files";

	private DocumentRepository documentRepository;
	private DocumentTypeRepository documentTypeRepository;
	private FileManagementInterface fileManagement;

	@Autowired
	public ManageDocumentService(DocumentRepository documentRepository, DocumentTypeRepository documentTypeRepository,
			DiskFile diskFile) {
		this.documentRepository = documentRepository;
		this.documentTypeRepository = documentTypeRepository;
		this.fileManagement = diskFile;
	}

	/**
	 * 
	 * @param userId
	 * @param page
	 * @param size
	 * @param order
	 * @return
	 */
	public Page<Document> getDocuments(Integer userId, Integer page, Integer size, String order) {

		Sort sort = order.equals("desc") ? Sort.by(Sort.Order.desc("createdAt")) : Sort.by(Sort.Order.asc("createdAt"));
		Pageable pageable = PageRequest.of(page, size, sort);

		return documentRepository.findByUserId(userId, pageable);
	}

	/**
	 * 
	 * @return
	 */
	public List<DocumentType> getDocumentTypes() {
		return documentTypeRepository.findAll();
	}

	/**
	 * 
	 * @param document
	 * @param documentTypeId
	 * @param userId
	 * @return
	 * @throws DocumentTypeNotFoundException
	 * @throws UploadFileException
	 * @throws FileExtensionException
	 */
	public Document saveDocument(Integer userId, MultipartFile file, Long documentTypeId)
			throws DocumentTypeNotFoundException, UploadFileException, FileExtensionException {

		Optional<DocumentType> documentTypeOptional = documentTypeRepository.findById(documentTypeId);
		if (documentTypeOptional.isPresent()) {
			DocumentType documentType = documentTypeOptional.get();

			String path = "/" + userId;
			String filename = file.getOriginalFilename();
			String diskname = UUID.randomUUID().toString();
			String contentType = file.getContentType();

			try {
				diskname = diskname.concat(getFileExtension(contentType));
			} catch (Exception e) {
				LOGGER.error("Error getting file extension {}", contentType);
				throw new FileExtensionException(String.format("Error getting file extension %s", contentType));
			}

			try {
				fileManagement.uploadFile(REPOSITORY + path, diskname, contentType, file.getInputStream());
			} catch (Exception e) {
				LOGGER.error("Error uploading path: {} file: {} userId: {}", path, filename, userId);
				throw new UploadFileException(
						String.format("Error uploading path: %s file: %s userId: %s", path, filename, userId));
			}

			Document document = new Document();
			document.setPath(path);
			document.setFilename(filename);
			document.setDiskName(diskname);
			document.setContentType(contentType);
			document.setUserId(userId);
			document.setType(documentType);

			return documentRepository.save(document);

		} else {
			LOGGER.error("Document type: {} not found", documentTypeId);
			throw new DocumentTypeNotFoundException(String.format("Document type: %s not found", documentTypeId));
		}

	}

	public DocumentData getDocument(Integer userId, Long documentId)
			throws DocumentNotFoundException, DownloadFileException {

		DocumentData documentData = null;

		Optional<Document> documentOptional = documentRepository.findByIdAndUserId(documentId, userId);
		if (documentOptional.isPresent()) {
			Document document = documentOptional.get();

			String path = REPOSITORY + document.getPath();
			String filename = document.getFilename();
			String contentType = document.getContentType();

			try {
				ByteArrayOutputStream baos = (ByteArrayOutputStream) fileManagement.downloadFile(path,
						document.getDiskName());
				documentData = new DocumentData(filename, contentType, baos.toByteArray());
			} catch (Exception e) {
				LOGGER.error("Error downloading path: {} filename: {} ", path, filename);
				throw new DownloadFileException(
						String.format("Error downloading path: %s filename: %s", path, filename));
			}

		} else {
			LOGGER.error("Document: {} not found for user {} ", documentId, userId);
			throw new DocumentNotFoundException(
					String.format("Document: %s not found for user: %s", documentId, userId));
		}
		return documentData;

	}

	private String getFileExtension(String contentType) throws MimeTypeException {
		MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
		MimeType type = allTypes.forName(contentType);
		return type.getExtension();
	}

}
