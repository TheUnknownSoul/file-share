package com.service;


import com.dto.SharedFileDto;
import com.dto.UserFilesDto;
import com.entity.File;
import com.exception.UserNotFoundException;
import com.repository.FileRepository;
import com.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;

@Service
public class FilesSharingServiceImpl implements FileService {

    @Autowired
    FileRepository fileRepository;
    @Autowired
    UserRepository userRepository;
    @Value("${root.directory}")
    private String rootDirectory;

    @Override
    public UserFilesDto getUserFiles(String email) throws FileNotFoundException, UserNotFoundException {
        if (userRepository.findByEmail(email).isPresent()) {
//           return fileRepository.findByName(email).orElseThrow(() ->new FileNotFoundException("File not found"));
            ModelMapper modelMapper = new ModelMapper();
//            modelMapper.map(userRepository.findByEmail(email));
            return null;

        } else {
            throw new UserNotFoundException("no such user");
        }
//        return null;
    }

    @Override
    public File findByFileId(Long fileId) throws FileNotFoundException {
        return fileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException("File not found"));

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
