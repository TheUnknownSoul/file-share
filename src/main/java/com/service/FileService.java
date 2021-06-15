package com.service;

import com.dto.FileDto;
import com.dto.FilesDto;
import com.dto.UserFilesDto;
import com.entity.UserFile;
import com.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;


public interface FileService {
    FilesDto getUserFiles(String email) throws Exception;

    ResponseEntity downloadFileByFileId(String fileId, Principal ownerEmail) throws IOException;

    String uploadFile(MultipartFile multipartFile, String email) throws IOException, UserNotFoundException;

    void shareFile(UserFilesDto filesDto) throws UserNotFoundException, FileNotFoundException;

}