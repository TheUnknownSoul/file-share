package com.service;


import com.dto.UserFilesDto;
import com.entity.File;
import com.entity.User;
import com.exception.UserNotFoundException;
import com.repository.FileRepository;
import com.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class FilesSharingServiceImpl implements FileService {

    @Value("${root.directory}")
    private static Path fileStorageLocation;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    ModelMapper modelMapper;

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
    public File findByFileId(int fileId) throws FileNotFoundException {
        return fileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException("File not found"));
    }

    @Override
    public Resource downloadFileByFileId(String fileId) throws IOException {
        if (fileRepository.findById(Integer.parseInt(fileId)).isPresent()) {
            Path p1 = Paths.get("src/files/");
//            Path filePath = p1.resolve().normalize();
            File byFileId = fileRepository.findByFileId(Integer.valueOf(fileId));
//            Resource resource = new UrlResource(String.valueOf(filePath));
            Resource resource = new UrlResource(p1.normalize() + "\\"+ String.valueOf(byFileId.getName()));
            if (resource.exists()) {
                return resource;
            }
        }
        throw new FileNotFoundException("File not found");
    }

    @Override
    public String uploadFile(MultipartFile uploadedFile, String email) throws IOException, UserNotFoundException {
        // need to add owned
        User user = userService.findByEmail(email);
        if (Objects.equals(uploadedFile.getContentType(), MediaType.TEXT_PLAIN_VALUE)) {
            if (userRepository.findByEmail(email).isPresent() && !uploadedFile.isEmpty()) {
                File file = new File();
                java.io.File directory = new java.io.File("src/files/");
                if (!directory.exists()) {
                    directory.mkdir();
                }

                Path path = Files.createFile(Paths.get("src/files/" + uploadedFile.getOriginalFilename()));
                Files.write(path, uploadedFile.getBytes());

                userRepository.findByEmail(email).get();
                ModelMapper mapper = new ModelMapper();
                file.setName(uploadedFile.getOriginalFilename());
                file.setUserEmail(email);
                file.setFileId(new Random().nextInt(1));
                mapper.map(file, File.class);
                List<File> owned = user.getOwned();
                owned.add(fileRepository.save(file));
                user.setOwned(owned);
                return String.valueOf(file.getFileId());
            }
        }
        return "File already exists";
    }

    @Override
    public void shareFile(String email, String fileId) throws UserNotFoundException {
        User user = userService.findByEmail(email);
        File file = fileRepository.findByFileId(Integer.parseInt(fileId));
        List<File> shared = user.getShared();
        List<File> owned = user.getOwned();
        shared.add(file);
        owned.add(file);
        userService.save(user);
    }
}
