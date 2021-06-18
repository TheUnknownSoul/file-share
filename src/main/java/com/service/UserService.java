package com.service;

import com.dto.UserDto;
import com.entity.User;
import com.exception.UserAlreadyRegisterException;
import com.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;


public interface UserService {
    boolean register(UserDto userDto);
}