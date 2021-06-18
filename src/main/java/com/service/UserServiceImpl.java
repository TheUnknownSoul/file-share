package com.service;

import com.dto.UserDto;
import com.entity.User;
import com.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
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
    ModelMapper modelMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public boolean register(UserDto userDto) {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if (!optionalUser.isPresent() && isUserInputValid(userDto)) {
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            User user = modelMapper.map(userDto, User.class);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    private boolean isUserInputValid(UserDto userDto) {
        return StringUtils.isNotBlank(userDto.getEmail()) && StringUtils.isNotBlank(userDto.getPassword());
    }
}
