package org.exception.demo.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyByte;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class ManageDocumentServiceTest {

	@Mock
	private DocumentRepository documentRepository;

	@Mock
	private DocumentTypeRepository documentTypeRepository;

	@Mock
	private DiskFile fileManagementInterface;

	@InjectMocks
	private ManageDocumentService manageDocumentervice;

	@Test
	public void getDocuments() {

		Page<Document> documents = Mockito.mock(Page.class);
		documents.and(new Document(1L, "path", "filename", "diskName", MediaType.TEXT_PLAIN_VALUE, 1, new DocumentType(1L, "type")));
		Pageable pageable = PageRequest.of(0, 1, Sort.by(Order.asc("createdAt")));
		Mockito.when(documentRepository.findByUserId(1, pageable)).thenReturn(documents);

		Page<Document> result = manageDocumentervice.getDocuments(1, 0, 1, "asc");

		assertEquals(result, documents);
	}

	@Test
	public void getDocumentTypes() {

		List<DocumentType> documentTypes = Mockito.mock(ArrayList.class);
		documentTypes.add(new DocumentType(1L, "type"));
		Mockito.when(documentTypeRepository.findAll()).thenReturn(documentTypes);

		List<DocumentType> result = manageDocumentervice.getDocumentTypes();

		assertEquals(result, documentTypes);
	}

	@Test
	public void saveDocument() throws DocumentTypeNotFoundException, UploadFileException, FileExtensionException {
		Optional<DocumentType> documentType = Optional.of(new DocumentType(1L, "type"));
		Mockito.when(documentTypeRepository.findById(1L)).thenReturn(documentType);

		MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE,
				"Hello, World!".getBytes());

		Mockito.when(documentRepository.save(Mockito.any(Document.class))).thenAnswer(item -> item.getArguments()[0]);

		Document document = manageDocumentervice.saveDocument(1, file, 1L);

		assertEquals(document.getContentType(), MediaType.TEXT_PLAIN_VALUE);
		assertEquals(document.getFilename(), "hello.txt");
	}

	@Test
	public void saveDocument_DocumentTypeNotFoundException() {
		Optional<DocumentType> documentType = Optional.empty();
		Mockito.when(documentTypeRepository.findById(1L)).thenReturn(documentType);

		MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE,
				"Hello, World!".getBytes());

		assertThrows(DocumentTypeNotFoundException.class, () -> manageDocumentervice.saveDocument(1, file, 1L));
	}

	@Test
	public void saveDocument_FileExtensionException() throws IOException, Exception {
		Optional<DocumentType> documentType = Optional.of(new DocumentType(1L, "type"));
		Mockito.when(documentTypeRepository.findById(1L)).thenReturn(documentType);

		MockMultipartFile file = new MockMultipartFile("file", "hello.txt", "WRONG_MEDIA_TYPE",
				"Hello, World!".getBytes());

		assertThrows(FileExtensionException.class, () -> manageDocumentervice.saveDocument(1, file, 1L));
	}
	
	@Test
	public void saveDocument_UploadFileException() throws IOException, Exception {
		Optional<DocumentType> documentType = Optional.of(new DocumentType(1L, "type"));
		Mockito.when(documentTypeRepository.findById(1L)).thenReturn(documentType);

		MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE,
				"Hello, World!".getBytes());

		Mockito.doThrow(new IOException("test")).when(fileManagementInterface).uploadFile(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.any(InputStream.class));

		assertThrows(UploadFileException.class, () -> manageDocumentervice.saveDocument(1, file, 1L));
	}
	
	@Test
	public void getDocument() throws Exception {
		Optional<Document> document = Optional.of(new Document(1L, "path", "filename", "diskName", MediaType.TEXT_PLAIN_VALUE, 1, new DocumentType(1L, "type")));
		Mockito.when(documentRepository.findByIdAndUserId(1L, 1)).thenReturn(document);
		
		Mockito.when(fileManagementInterface.downloadFile(Mockito.anyString(), Mockito.anyString())).thenReturn(new ByteArrayOutputStream());
		
		DocumentData documentData = manageDocumentervice.getDocument(1, 1L);
		
		assertEquals(documentData.getFilename(), "filename");
		assertEquals(documentData.getContentType(), MediaType.TEXT_PLAIN_VALUE);
		
	}

	@Test
	public void getDocument_DocumentNotFoundException() {
		Optional<Document> document = Optional.empty();
		Mockito.when(documentRepository.findByIdAndUserId(1L, 1)).thenReturn(document);
		
		assertThrows(DocumentNotFoundException.class, () -> manageDocumentervice.getDocument(1, 1L));
	}

	
	@Test
	public void getDocument_DownloadFileException() throws Exception {
		Optional<Document> document = Optional.of(new Document(1L, "path", "filename", "diskName", MediaType.TEXT_PLAIN_VALUE, 1, new DocumentType(1L, "type")));
		Mockito.when(documentRepository.findByIdAndUserId(1L, 1)).thenReturn(document);
		
		Mockito.doThrow(new IOException("test")).when(fileManagementInterface).downloadFile(Mockito.anyString(),Mockito.anyString());
		
		assertThrows(DownloadFileException.class, () -> manageDocumentervice.getDocument(1, 1L));
	}
}
