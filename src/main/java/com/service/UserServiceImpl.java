package com.service;

import com.dto.UserDto;
import com.entity.User;
import com.exception.UserAlreadyRegisterException;
import com.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    public ResponseEntity register(UserDto userDto) throws UserAlreadyRegisterException {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(userDto.getEmail()));
        if (!optionalUser.isPresent()) {
            ModelMapper modelMapper = new ModelMapper();
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            modelMapper.validate();
            User user = modelMapper.map(userDto, User.class);
            this.save(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            throw new UserAlreadyRegisterException("This user has already been registered");
        }
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
