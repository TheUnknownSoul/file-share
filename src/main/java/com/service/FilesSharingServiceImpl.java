package com.service;


import com.dto.FileDto;
import com.entity.User;
import com.entity.UserFile;
import com.exception.UserNotFoundException;
import com.repository.FileRepository;
import com.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilesSharingServiceImpl implements FileService {


    private static final Path FILE_STORAGE_LOCATION = Paths.get("src/files/");
    @Autowired
    FileRepository fileRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public FileDto getUserFiles(String email) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException(HttpStatus.BAD_REQUEST, "no such user");
        }
        User user = optionalUser.get();
        List<FileDto> sharedFiles = user.getOwned().stream()
                .filter(UserFile::isShared)
                .map(userFile -> modelMapper.map(userFile, FileDto.class))
                .collect(Collectors.toList());
        List<FileDto> hiddenFiles = user.getOwned().stream()
                .filter(userFile -> !userFile.isShared())
                .map(userFile -> modelMapper.map(userFile, FileDto.class))
                .collect(Collectors.toList());
        if (sharedFiles.isEmpty() && hiddenFiles.isEmpty()) {
            throw new NullPointerException("no files to show");
        }
        return FileDto.builder()
                .shared(sharedFiles)
                .hidden(hiddenFiles)
                .build();
    }


    @Override
    public ResponseEntity<byte[]> downloadFileByFileId(String fileId) throws IOException {
        Optional<UserFile> fileOptional = fileRepository.findById(Integer.valueOf(fileId));
        SecurityContext securityContextHolder = SecurityContextHolder.getContext();
        if (fileOptional.isPresent()) {
            Optional<User> optionalUser = userRepository.findByEmail(securityContextHolder.getAuthentication().getName());
            if (optionalUser.isPresent()) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (isUserAuthenticated(authentication)) {
                    if (fileOptional.get().isShared() || fileOptional.get().getUser().equals(optionalUser.get())) {
                        Path path = Paths.get(String.valueOf(FILE_STORAGE_LOCATION));
                        return ResponseEntity.ok()
                                .contentType(MediaType.TEXT_PLAIN)
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileOptional.get().getFileName() + "\"")
                                .body(Files.readAllBytes(Paths.get(path + "/" + fileOptional.get().getFileName())));
                    }

                } else {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }

        }
        return null;
    }

    @Override
    public String uploadFile(MultipartFile uploadedFile, String email) throws IOException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (optionalUser.isPresent() && isUserAuthenticated(authentication)) {
            if (Objects.equals(uploadedFile.getContentType(), MediaType.TEXT_PLAIN_VALUE)) {
                if (!uploadedFile.isEmpty()) {
                    File directory = new File(String.valueOf(FILE_STORAGE_LOCATION));
                    if (isDirectoryExists(directory)) {
                        Path path = Files.createFile(Paths.get(FILE_STORAGE_LOCATION + "/" + uploadedFile.getOriginalFilename()));
                        Files.write(path, uploadedFile.getBytes());
                        UserFile userFile = UserFile.builder()
                                .isShared(false)
                                .fileName(uploadedFile.getOriginalFilename())
                                .user(optionalUser.get())
                                .build();
                        UserFile savedFile = fileRepository.save(userFile);
                        return "File id: " + savedFile.getFileId();

                    }
                }
            }
        }

        return "File already exists";
    }

    @Override
    public void shareFile(String ownerEmail, String fileId) {
        Optional<UserFile> optionalFile = fileRepository.findById(Integer.valueOf(fileId));
        Optional<User> optionalUser = userRepository.findByEmail(ownerEmail);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (optionalUser.isPresent() && optionalFile.isPresent() && isUserAuthenticated(authentication)) {
            optionalFile.get().setShared(true);
            fileRepository.save(optionalFile.get());
        }
    }

    private boolean isDirectoryExists(File directory) {
        if (!directory.exists()) {
            return directory.mkdir();
        }
        return false;
    }

    private boolean isUserAuthenticated(Authentication authentication) {
        return authentication.isAuthenticated();
    }
}
