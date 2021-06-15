package com.service;

import com.dto.UserDto;
import com.entity.User;
import com.exception.UserAlreadyRegisterException;
import com.exception.UserNotFoundException;
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
    @Autowired
    ModelMapper modelMapper;


    public ResponseEntity register(UserDto userDto) throws UserAlreadyRegisterException {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if (!optionalUser.isPresent() && userDto.getEmail() != null && !userDto.getEmail().equals("")) {
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
//            modelMapper.validate();
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
    public User findByEmail(String email) throws UserNotFoundException {

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()){
            return user.get();
        }
        throw new UserNotFoundException("No such user");
    }
}
