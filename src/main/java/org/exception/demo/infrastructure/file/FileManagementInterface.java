package org.exception.demo.infrastructure.file;

import java.io.InputStream;
import java.io.OutputStream;

public interface FileManagementInterface {

	public void uploadFile(String path, String filename, String contentType, InputStream inputStream) throws Exception;

	public OutputStream downloadFile(String path, String filename) throws Exception;

}
