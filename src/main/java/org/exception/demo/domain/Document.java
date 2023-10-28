package org.exception.demo.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "document")
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	@Column
	private String path;

	@Column
	private String filename;

	@Column
	private String diskName;

	@Column
	private String contentType;

	@Column
	private Integer userId;

	@ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private DocumentType type;

	private Date createdAt;

	@PrePersist
	private void prePersist() {
		this.createdAt = new Date();
	}

	public Document() {

	}

	public Document(Long id, String path, String filename, String diskName, String contentType, Integer userId,
			DocumentType type) {
		this.id = id;
		this.path = path;
		this.filename = filename;
		this.diskName = diskName;
		this.contentType = contentType;
		this.userId = userId;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getDiskName() {
		return diskName;
	}

	public void setDiskName(String diskName) {
		this.diskName = diskName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public DocumentType getType() {
		return type;
	}

	public void setType(DocumentType type) {
		this.type = type;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

}