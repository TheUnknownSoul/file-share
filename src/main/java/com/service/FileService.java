package com.service;

import com.dto.SharedFileDto;
import com.entity.File;
import com.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


public interface FileService {
    List<File> getUserFiles(String email) throws FileNotFoundException, UserNotFoundException;

    File findByFileId(int fileId) throws FileNotFoundException;

    ResponseEntity downloadFileByFileId(String fileId) throws IOException;

    String uploadFile(MultipartFile multipartFile, String email) throws IOException, UserNotFoundException;

    void shareFile(SharedFileDto sharedFileDto) throws UserNotFoundException, com.exception.FileNotFoundException;

}