package com.service;


import com.dto.SharedFileDto;
import com.dto.UserFilesDto;
import com.entity.File;
import com.repository.FileRepository;
import com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FilesSharingServiceImpl implements FileService{

    @Autowired
    FileRepository fileRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public UserFilesDto getUserFiles(String email) {
        if (userRepository.findByEmail(email).isPresent()){

        }
        return null;
    }

    @Override
    public File findByFileId(String fileId) {
        return null;
    }

    @Override
    public ResponseEntity<File> downloadFileByFileId(String fileId) throws IOException {
        return null;
    }

    @Override
    public String uploadFile(MultipartFile multipartFile, String email) throws IOException {
        return null;
    }

    @Override
    public void shareFile(SharedFileDto sharedFileDto) {

    }
}
