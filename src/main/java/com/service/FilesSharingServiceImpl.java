package com.service;


import com.dto.FileDto;
import com.dto.SharedFileDto;
import com.dto.UserFilesDto;
import com.entity.File;
import com.entity.User;
import com.exception.UserNotFoundException;
import com.oracle.webservices.internal.api.message.ContentType;
import com.repository.FileRepository;
import com.repository.UserRepository;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FilesSharingServiceImpl implements FileService {

    @Autowired
    FileRepository fileRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServiceImpl userService;
    @Autowired
    ModelMapper modelMapper;

    @Value("${root.directory}")
    private String rootDirectory;

    private static Path fileStorageLocation;
    @Override
    public UserFilesDto getUserFiles(String email) throws FileNotFoundException, UserNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            UserFilesDto userFilesDto = modelMapper.map(user, UserFilesDto.class);
            if (userFilesDto != null) {
                return userFilesDto;
            } else {
                throw new FileNotFoundException("Files not found");
            }
        } else {
            throw new UserNotFoundException("no such user");
        }
    }

    @Override
    public File findByFileId(Long fileId) throws FileNotFoundException {
        return fileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException("File not found"));
    }

    @Override
    public Resource downloadFileByFileId(String fileId) throws IOException {
        if (fileRepository.findById(Long.valueOf(fileId)).isPresent()) {
            Path filePath = fileStorageLocation.resolve(fileId).normalize();
            Resource resource = new UrlResource(String.valueOf(filePath));
            if (resource.exists()){
                return resource;
            }
        }
        return null;
    }

    @Override
    public String uploadFile(MultipartFile uploadedFile, String email) throws IOException, UserNotFoundException {
        // need to add owned
        User user = userService.findByEmail(email);
        if (Objects.equals(uploadedFile.getContentType(), MediaType.TEXT_PLAIN_VALUE)) {
            if (userRepository.findByEmail(email).isPresent() && !uploadedFile.isEmpty()) {
                File file = new File();
                Path path = Paths.get(rootDirectory + "/" + uploadedFile.getOriginalFilename());
                Files.createDirectory(path);
                Files.createFile(Paths.get(Objects.requireNonNull(uploadedFile.getOriginalFilename())));
                Files.write(path, uploadedFile.getBytes());
                userRepository.findByEmail(email).get();
                fileRepository.save(file);
                return fileRepository.findById(file.getId()).toString();
            }
        }
        return "File already exists";
    }

    @Override
    public void shareFile(SharedFileDto sharedFileDto) {

    }
}
