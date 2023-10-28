package org.exception.demo.infrastructure.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DiskFile implements FileManagementInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(DiskFile.class);

	private static final int BUFFER_SIZE = 1024;

	@Override
	public void uploadFile(String path, String filename, String contentType, InputStream inputStream) throws Exception {
		FileOutputStream fos = null;
		try {
			LOGGER.info("uploadFile path: {} filename: {} contentType: {}", path, filename, contentType);

			Path directory = Paths.get(path);
			Files.createDirectories(directory);

			int length;
			byte[] buffer = new byte[BUFFER_SIZE];
			fos = new FileOutputStream(new File(path + "/" + filename));
			//fos.write(inputStream.readAllBytes());
			while (-1 != (length = inputStream.read(buffer, 0, BUFFER_SIZE))) {
				fos.write(buffer, 0, length);
			}
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
					throw e;
				}
			}
		}
	}

	@Override
	public OutputStream downloadFile(String path, String filename) throws Exception {
		ByteArrayOutputStream baos = null;
		FileInputStream fis = null;
		try {
			LOGGER.info("downloadFile path: {} filename: {}", path, filename);
			
			fis = new FileInputStream(new File(path + "/" + filename));

			int length;
			byte[] buffer = new byte[BUFFER_SIZE];
			baos = new ByteArrayOutputStream();
			//baos.writeBytes(fis.readAllBytes());
			while (-1 != (length = fis.read(buffer, 0, BUFFER_SIZE))) {
				baos.write(buffer, 0, length);
			}
			
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
					throw e;
				}
			}
		}

		return baos;
	}

}
