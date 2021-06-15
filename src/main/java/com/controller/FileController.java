package com.controller;


import com.dto.FileDto;
import com.dto.FilesDto;
import com.dto.UserFilesDto;
import com.entity.User;
import com.exception.UserNotFoundException;
import com.service.FilesSharingServiceImpl;
import com.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class FileController {

    @Autowired
    FilesSharingServiceImpl sharingService;

    @Autowired
    UserServiceImpl userService;


    @GetMapping("/file")
    public ResponseEntity<FilesDto> getUsersFiles( Principal principal) throws Exception {
        return new ResponseEntity<>(sharingService.getUserFiles(principal.getName()), HttpStatus.OK);
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<byte[]> getFileById(@PathVariable String fileId,Principal principal) throws IOException {
        return sharingService.downloadFileByFileId(fileId,principal);
    }

    @PostMapping("/file")
    public String uploadFile(@RequestParam("file") MultipartFile uploadedFile,  Principal user) throws IOException, UserNotFoundException {
        return sharingService.uploadFile(uploadedFile, user.getName());
    }

    @PostMapping("/share")
    @ResponseStatus(HttpStatus.OK)
    public void shareFile(@RequestBody UserFilesDto filesDto) {
        sharingService.shareFile(filesDto);

    }
}
