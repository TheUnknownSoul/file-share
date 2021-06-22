package com.controller;


import com.dto.FileDto;
import com.exception.CustomException;
import com.repository.FileRepository;
import com.service.FileService;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class FileController {

    @Autowired
    FileService sharingService;

    @Autowired
    UserService userService;

    @Autowired
    FileRepository fileRepository;

    @GetMapping(value = "/file", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileDto> getUsersFiles() throws Exception {
        return new ResponseEntity<>(sharingService.getUserFiles(), HttpStatus.OK);
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<byte[]> getFileById(@PathVariable String fileId) throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileRepository.findById(Integer.valueOf(fileId)) + "\"")
                .body(sharingService.downloadFileByFileId(fileId));
    }

    @PostMapping(value = "/file", produces = MediaType.APPLICATION_JSON_VALUE)
    public String uploadFile(@RequestParam("file") MultipartFile uploadedFile) throws IOException, CustomException {
        return sharingService.uploadFile(uploadedFile);
    }

    @PostMapping(value = "/share", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void shareFile(@RequestParam(name = "name") String ownerEmail, @RequestParam(name = "id") String fileId) throws FileNotFoundException, CustomException {
        sharingService.shareFile(ownerEmail, fileId);
    }
}