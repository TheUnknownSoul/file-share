package com.controller;


import com.dto.SharedFileDto;
import com.dto.UserDto;
import com.dto.UserFilesDto;
import com.exception.UserNotFoundException;
import com.service.FilesSharingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class FileController {

    @Autowired
    FilesSharingServiceImpl sharingService;

    @GetMapping("/file")
    public ResponseEntity<UserFilesDto> getUsersFiles(Principal principal) throws FileNotFoundException, UserNotFoundException {
        if (principal instanceof UserDto) {
            return new ResponseEntity<>(sharingService.getUserFiles(((UserDto) principal).getEmail()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/file/{fileId}")
    public Resource getFileById(@PathVariable String fileId) throws IOException {
        return sharingService.downloadFileByFileId(fileId);
    }

    @PostMapping("/file")
    public String uploadFile(MultipartFile uploadedFile, String ownerEmail) throws IOException, UserNotFoundException {
        return sharingService.uploadFile(uploadedFile, ownerEmail);
    }

    @PostMapping("/share")
    public void shareFile(@RequestBody SharedFileDto fileDto) {
        sharingService.shareFile(fileDto);

    }
}
