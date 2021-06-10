package com.controller;


import com.dto.SharedFileDto;
import com.entity.File;
import com.exception.UserNotFoundException;
import com.service.FilesSharingServiceImpl;
import com.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FileController {

    @Autowired
    FilesSharingServiceImpl sharingService;

    @Autowired
    UserServiceImpl userService;


    @GetMapping("/file")
    public ResponseEntity<List<File>> getUsersFiles(Principal principal) throws UserNotFoundException {
        //need to refactor ability to check users principal
//        if (principal.getName().equals(userService.findByEmail(principal.getName()))) {
            return new ResponseEntity<>(sharingService.getUserFiles(principal.getName()), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<byte[]> getFileById(@PathVariable String fileId) throws IOException {
        return sharingService.downloadFileByFileId(fileId);
    }

    @PostMapping("/file")
    public String uploadFile(@RequestParam("file") MultipartFile uploadedFile, Principal ownerEmail) throws IOException, UserNotFoundException {
        return sharingService.uploadFile(uploadedFile, ownerEmail.getName());
    }

    @PostMapping("/share")
    public void shareFile(@RequestBody SharedFileDto sharedFileDto) throws UserNotFoundException, com.exception.FileNotFoundException {
        sharingService.shareFile(sharedFileDto);

    }
}
