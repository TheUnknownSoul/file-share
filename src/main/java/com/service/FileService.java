package com.service;

import com.dto.UserFilesDto;
import com.entity.File;
import com.exception.UserNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;


public interface FileService {
    UserFilesDto getUserFiles(String email) throws FileNotFoundException, UserNotFoundException;

    File findByFileId(int fileId) throws FileNotFoundException;

    Resource downloadFileByFileId(String fileId) throws IOException;

    String uploadFile(MultipartFile multipartFile, String email) throws IOException, UserNotFoundException;

    void shareFile(String email, String fileId) throws UserNotFoundException;

}