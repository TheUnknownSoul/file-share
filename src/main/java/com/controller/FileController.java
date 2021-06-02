package com.controller;


import com.dto.UserFilesDto;
import com.entity.File;
import com.exception.UserNotFoundException;
import com.service.FilesSharingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class FileController {

    @Autowired
    FilesSharingServiceImpl sharingService;

    @GetMapping("/file")
    public ResponseEntity<UserFilesDto> getUsersFiles(String email) throws FileNotFoundException, UserNotFoundException {
       return new ResponseEntity<>(sharingService.getUserFiles(email),HttpStatus.OK);
    }
}
