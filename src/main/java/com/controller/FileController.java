package com.controller;


import com.dto.FileDto;
import com.exception.UserNotFoundException;
import com.service.FileService;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class FileController {

    @Autowired
    FileService sharingService;

    @Autowired
    UserService userService;


    @GetMapping(value = "/file", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileDto> getUsersFiles(Principal principal) throws Exception {
        return new ResponseEntity<>(sharingService.getUserFiles(principal.getName()), HttpStatus.OK);
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<byte[]> getFileById(@PathVariable String fileId) throws IOException {
        return sharingService.downloadFileByFileId(fileId);
    }

    @PostMapping(value = "/file", produces = MediaType.APPLICATION_JSON_VALUE)
    public String uploadFile(@RequestParam("file") MultipartFile uploadedFile, Principal user) throws IOException, UserNotFoundException {
        return sharingService.uploadFile(uploadedFile, user.getName());
    }

    @PostMapping(value = "/share", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void shareFile(@RequestParam(name = "name") String ownerEmail, @RequestParam(name = "id") String fileId) throws FileNotFoundException, UserNotFoundException {
        sharingService.shareFile(ownerEmail, fileId);

    }
}
