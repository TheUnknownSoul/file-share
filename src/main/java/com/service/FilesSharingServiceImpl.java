package com.service;


import com.dto.FileDto;
import com.dto.FilesDto;
import com.dto.UserFilesDto;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
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
    UserServiceImpl userService;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public FilesDto getUserFiles(String email) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException("no such user");
        }
        User user = optionalUser.get();
        List<FileDto> sharedFiles = user.getOwned().stream()
                .filter(userFile -> userFile.isShared() && userFile.getName() != null)
                .map(userFile -> modelMapper.map(userFile, FileDto.class))
                .collect(Collectors.toList());
        List<FileDto> hiddenFiles = user.getOwned().stream()
                .filter(userFile -> !userFile.isShared() && userFile.getName() != null)
                .map(userFile -> modelMapper.map(userFile, FileDto.class))
                .collect(Collectors.toList());
        if (sharedFiles.size() == 0 && hiddenFiles.size() == 0) {
            throw new Exception("No uploaded files");
        } else if (sharedFiles.size() == 0 && hiddenFiles.size() > 0) {
            return FilesDto.builder()
                    .hidden(hiddenFiles)
                    .build();
        } else if (sharedFiles.size() > 0 && hiddenFiles.size() == 0) {
            return FilesDto.builder()
                    .shared(sharedFiles)
                    .build();
        }
        return FilesDto.builder()
                .shared(sharedFiles)
                .hidden(hiddenFiles)
                .build();
    }


    @Override
    public ResponseEntity<byte[]> downloadFileByFileId(String fileId, Principal ownerEmail) throws IOException {
        Optional<UserFile> fileOptional = fileRepository.findById(Integer.valueOf(fileId));
        Optional<User> optionalUser = userRepository.findByEmail(ownerEmail.getName());
        if (fileOptional.isPresent() && optionalUser.isPresent()) {
            Path path = Paths.get(String.valueOf(FILE_STORAGE_LOCATION));
            if (fileOptional.get().isShared() || optionalUser.get().getOwned().size() > 0) {

                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileOptional.get().getName() + "\"")
                        .body(Files.readAllBytes(Paths.get(path + "/" + fileOptional.get().getName())));

            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        throw new FileNotFoundException("File not found");
    }

    @Override
    public String uploadFile(MultipartFile uploadedFile, String email) throws IOException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            if (Objects.equals(uploadedFile.getContentType(), MediaType.TEXT_PLAIN_VALUE)) {
                if (!uploadedFile.isEmpty()) {
                    UserFile userFile = new UserFile();
                    File directory = new File(String.valueOf(FILE_STORAGE_LOCATION));
                    if (!directory.exists()) {
                        if (directory.mkdir()) {
                            Path path = Files.createFile(Paths.get(FILE_STORAGE_LOCATION + "/" + uploadedFile.getOriginalFilename()));
                            Files.write(path, uploadedFile.getBytes());
                            userFile.setName(uploadedFile.getOriginalFilename());
                            userFile.setFileId(userFile.getFileId());
                            List<UserFile> owned = optionalUser.get().getOwned();
                            owned.add(fileRepository.save(userFile));
                            optionalUser.get().setOwned(owned);
                            userService.save(optionalUser.get());
                            return String.valueOf(userFile.getFileId());
                        }
                    }
                }
            }
        }
        return "File already exists";
    }

    @Override
    public void shareFile(UserFilesDto filesDto) {
        Optional<UserFile> optionalFile = fileRepository.findById(filesDto.getId());
        Optional<User> optionalUser = userRepository.findByEmail(filesDto.getName());
        if (optionalUser.isPresent() && optionalFile.isPresent()) {
            optionalFile.get().setShared(true);
            fileRepository.save(optionalFile.get());
        }
    }
}
