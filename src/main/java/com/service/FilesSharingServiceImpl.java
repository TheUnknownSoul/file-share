package com.service;


import com.dto.FileDto;
import com.dto.FilesDto;
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


    private static final Path ROOT_PATH = Paths.get("src/files");
    private static final String FILES_STORAGE_LOCATION = String.valueOf(Paths.get(ROOT_PATH + "/%s"));

    private static final String USER_NOT_FOUND_MESSAGE = "No such user";
    private static final String ACCESS_DENIED_MESSAGE = "Access denied";
    private static final String FILE_NOT_FOUND_MESSAGE = "File not found";
    private static final String FILE_EXISTS_MESSAGE = "File exists";
    private static final String SHARING_FILE_FORBIDDEN_MESSAGE = "Sharing this file is forbidden for you";

    @Autowired
    FileRepository fileRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    ModelMapper modelMapper;


    @Override
    public FilesDto getUserFiles() {
        Optional<User> optionalUser = userRepository.findByEmail(checkAuthenticationAndReturnEmail());
        if (!optionalUser.isPresent()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, USER_NOT_FOUND_MESSAGE);
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
        return FilesDto.builder()
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
                return Files.readAllBytes(Paths.get(String.format(FILES_STORAGE_LOCATION, fileOptional.get().getFileName())));
            }
        } else {
            throw new CustomException(HttpStatus.FORBIDDEN, ACCESS_DENIED_MESSAGE);
        }
        throw new CustomException(HttpStatus.BAD_REQUEST, FILE_NOT_FOUND_MESSAGE);
    }

    @Override
    public String uploadFile(MultipartFile uploadedFile) throws IOException {
        Optional<User> optionalUser = userRepository.findByEmail(checkAuthenticationAndReturnEmail());
        if (!optionalUser.isPresent()) {
            throw new CustomException(HttpStatus.FORBIDDEN, USER_NOT_FOUND_MESSAGE);
        }
        if (Objects.equals(uploadedFile.getContentType(), MediaType.TEXT_PLAIN_VALUE) && !uploadedFile.isEmpty()) {
            File directory = new File(String.valueOf(ROOT_PATH));
            if (isDirectoryExists(directory)) {
                Path path = Files.createFile(Paths.get(String.format(FILES_STORAGE_LOCATION, uploadedFile.getOriginalFilename())));
                Files.write(path, uploadedFile.getBytes());
                UserFile userFile = UserFile.builder()
                        .isShared(false)
                        .fileName(uploadedFile.getOriginalFilename())
                        .user(optionalUser.get())
                        .build();
                userFile = fileRepository.save(userFile);
                return "File id: " + userFile.getFileId();
            }
        }
        return FILE_EXISTS_MESSAGE;
    }

    @Override
    public void shareFile(String ownerEmail, String fileId) {
        Optional<UserFile> optionalFile = fileRepository.findById(Integer.valueOf(fileId));
        Optional<User> optionalUser = userRepository.findByEmail(ownerEmail);
        if (!optionalUser.isPresent() || !optionalFile.isPresent() ||
                !optionalUser.get().getEmail().equals(checkAuthenticationAndReturnEmail())
                || !optionalFile.get().getUser().getEmail().equals(ownerEmail)) {
            throw new CustomException(HttpStatus.FORBIDDEN, SHARING_FILE_FORBIDDEN_MESSAGE);
        }
        UserFile file = optionalFile.get();
        file.setShared(true);
        fileRepository.save(file);
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
        throw new CustomException(HttpStatus.FORBIDDEN, ACCESS_DENIED_MESSAGE);
    }
}
