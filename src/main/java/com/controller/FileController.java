package com.controller;


import com.dto.UserFilesDto;
import com.entity.File;
import com.service.FilesSharingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {

    @Autowired
    FilesSharingServiceImpl sharingService;

    @GetMapping("/api/file")
    public UserFilesDto getUsersFiles(String email){
       return sharingService.getUserFiles(email);
    }

}
