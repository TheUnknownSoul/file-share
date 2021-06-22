package com.service;

import com.dto.FileDto;
import com.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;


public interface FileService {
    FileDto getUserFiles() throws Exception;

    byte[] downloadFileByFileId(String fileId) throws IOException;

    String uploadFile(MultipartFile multipartFile) throws IOException, CustomException;

    void shareFile(String ownerEmail, String fileId) throws CustomException, FileNotFoundException;

}