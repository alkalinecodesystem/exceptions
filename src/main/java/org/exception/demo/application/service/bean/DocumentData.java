package org.exception.demo.application.service.bean;

public class DocumentData {

	private String filename;

	private String contentType;

	private byte[] content;

	public DocumentData(String filename, String contentType, byte[] content) {
		this.filename = filename;
		this.contentType = contentType;
		this.content = content;
	}

	public String getFilename() {
		return filename;
	}

	public String getContentType() {
		return contentType;
	}

	public byte[] getContent() {
		return content;
	}

}
