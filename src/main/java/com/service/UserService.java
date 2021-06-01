package com.service;

import com.dto.UserDto;
import com.exception.UserNotFoundException;


public interface UserService {
    void register(UserDto userDto) throws UserNotFoundException;
}