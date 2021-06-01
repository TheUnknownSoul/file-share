package com.service;

import com.dto.SharedFileDto;
import com.dto.UserFilesDto;
import com.entity.File;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    UserFilesDto getUserFiles(String email);

    File findByFileId(String fileId);

    ResponseEntity<File> downloadFileByFileId(String fileId) throws IOException;

    String uploadFile(MultipartFile multipartFile, String email) throws IOException;

    void shareFile(SharedFileDto sharedFileDto);

}
