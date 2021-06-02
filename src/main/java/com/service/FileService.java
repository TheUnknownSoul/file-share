package com.service;

import com.dto.SharedFileDto;
import com.dto.UserFilesDto;
import com.entity.File;
import com.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;

public interface FileService {
    UserFilesDto getUserFiles(String email) throws FileNotFoundException, UserNotFoundException;

    File findByFileId(Long fileId) throws FileNotFoundException;

    ResponseEntity<File> downloadFileByFileId(String fileId) throws IOException;

    String uploadFile(MultipartFile multipartFile, String email) throws IOException;

    void shareFile(SharedFileDto sharedFileDto);


}
