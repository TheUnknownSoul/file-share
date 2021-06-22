package com.service;


import com.dto.FileDto;
import com.entity.User;
import com.entity.UserFile;
import com.exception.CustomException;
import com.repository.FileRepository;
import com.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
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


    private static final Path FILE_STORAGE_LOCATION = Paths.get("src/files");

    @Autowired
    FileRepository fileRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public FileDto getUserFiles() {
        Optional<User> optionalUser = userRepository.findByEmail(checkAuthenticationAndReturnEmail());
        if (!optionalUser.isPresent()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "no such user");
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
            throw new CustomException(HttpStatus.OK, "no files to show");
        }
        return FileDto.builder()
                .shared(sharedFiles)
                .hidden(hiddenFiles)
                .build();
    }

    @Override
    public byte[] downloadFileByFileId(String fileId) throws IOException {
        Optional<UserFile> fileOptional = fileRepository.findById(Integer.valueOf(fileId));
        Optional<User> optionalUser = userRepository.findByEmail(checkAuthenticationAndReturnEmail());
            if (optionalUser.isPresent() && fileOptional.isPresent()) {
                if (fileOptional.get().isShared() || fileOptional.get().getUser().equals(optionalUser.get())) {
                    Path path = Paths.get(String.valueOf(FILE_STORAGE_LOCATION));
                    return Files.readAllBytes(Paths.get(path + "/" + fileOptional.get().getFileName()));
                }
            } else {
                throw new CustomException(HttpStatus.FORBIDDEN, " Access denied");
            }
        throw new CustomException(HttpStatus.BAD_REQUEST, "file doesnt exists");
    }

    @Override
    public String uploadFile(MultipartFile uploadedFile) throws IOException {
        Optional<User> optionalUser = userRepository.findByEmail(checkAuthenticationAndReturnEmail());
        if (optionalUser.isPresent()) {
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
        if (optionalUser.isPresent() && optionalFile.isPresent() &&
                optionalUser.get().getEmail().equals(checkAuthenticationAndReturnEmail())) {
            optionalFile.get().setShared(true);
            fileRepository.save(optionalFile.get());
            return;
        }
        throw new CustomException(HttpStatus.FORBIDDEN, "You cannot share this file or request invalid params");
    }

    private boolean isDirectoryExists(File directory) {
        if (!directory.exists()) {
            return directory.mkdir();
        }
        return false;
    }

    private String checkAuthenticationAndReturnEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new CustomException(HttpStatus.FORBIDDEN, "Access denied");
    }
}
