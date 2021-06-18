package com.service;

import com.dto.FileDto;
import com.dto.UserFilesDto;
import com.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;


public interface FileService {
    FileDto getUserFiles(String email) throws Exception;

    ResponseEntity downloadFileByFileId(String fileId) throws IOException;

    String uploadFile(MultipartFile multipartFile, String email) throws IOException, UserNotFoundException;

    void shareFile(String ownerEmail, String fileId) throws UserNotFoundException, FileNotFoundException;

}