package com.service;

import com.dto.UserDto;
import com.entity.User;
import com.exception.UserNotFoundException;
import com.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    public void register(UserDto userDto) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if (!optionalUser.isPresent()) {
            ModelMapper modelMapper = new ModelMapper();
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            modelMapper.validate();
            User user = modelMapper.map(userDto, User.class);
            userRepository.save(user);
        }else{
            throw new UserNotFoundException("No such user has been registered");
        }
    }
}
