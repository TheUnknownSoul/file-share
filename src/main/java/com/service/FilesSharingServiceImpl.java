package com.service;


import com.dto.SharedFileDto;
import com.dto.UserFilesDto;
import com.entity.File;
import com.entity.User;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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
    public List<File> getUserFiles(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email);
        UserFilesDto userFilesDto = modelMapper.map(user, UserFilesDto.class);
        if (userFilesDto != null) {
            return userService.findByEmail(email).getShared();
        } else {
            throw new UserNotFoundException("no such user");
        }
    }

    @Override
    public File findByFileId(int fileId) throws FileNotFoundException {
        return fileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException("File not found"));
    }

    @Override
    public ResponseEntity<byte[]> downloadFileByFileId(String fileId) throws IOException {
        File file = fileRepository.findByFileId(Integer.valueOf(fileId));
        User user = userRepository.findByEmail(file.getUserEmail());
        if (fileRepository.findById(Integer.parseInt(fileId)).isPresent()) {
            Path p1 = Paths.get(String.valueOf(FILE_STORAGE_LOCATION));
            if (user.getShared().contains(file)) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .body(Files.readAllBytes(Paths.get(p1 + "/" + file.getName())));
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        throw new FileNotFoundException("File not found");
    }

    @Override
    public String uploadFile(MultipartFile uploadedFile, String email) throws IOException, UserNotFoundException {
        User user = userService.findByEmail(email);
        if (Objects.equals(uploadedFile.getContentType(), MediaType.TEXT_PLAIN_VALUE)) {
            if (userRepository.findByEmail(email) != null && !uploadedFile.isEmpty()) {
                File file = new File();
                java.io.File directory = new java.io.File(String.valueOf(FILE_STORAGE_LOCATION));
                if (!directory.exists()) {
                    directory.mkdir();
                }
                Path path = Files.createFile(Paths.get(FILE_STORAGE_LOCATION + "/" + uploadedFile.getOriginalFilename()));
                Files.write(path, uploadedFile.getBytes());
                file.setName(uploadedFile.getOriginalFilename());
                file.setUserEmail(email);
                file.setFileId(new Random().nextInt(1));
//                SharedFileDto map1 = modelMapper.map(file, SharedFileDto.class);
//                System.out.println(map1.getId() + " " + map1.getName());
                List<File> owned = user.getOwned();
                owned.add(fileRepository.save(file));
                user.setOwned(owned);
                userService.save(user);
                return String.valueOf(file.getFileId());
            }
        }
        return "File already exists";
    }

    @Override
    public void shareFile(SharedFileDto sharedFileDto) throws UserNotFoundException, com.exception.FileNotFoundException {
        User user = userService.findByEmail(sharedFileDto.getName());
        List<File> owned = user.getOwned();
        List<File> shared = user.getShared();
        File file = owned.stream()
                .filter(f -> f.getFileId() == sharedFileDto.getId())
                .findFirst()
                .map(f -> {
                    owned.remove(f);
                    return f;
                }).orElseThrow(() -> new com.exception.FileNotFoundException("File not found"));
        shared.add(file);
        user.setOwned(owned);
        user.setShared(shared);
        userService.save(user);

    }
}
