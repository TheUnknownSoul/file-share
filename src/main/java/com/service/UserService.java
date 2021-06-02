package com.service;

import com.dto.UserDto;
import com.entity.User;
import com.exception.UserAlreadyRegisterException;
import com.exception.UserNotFoundException;


public interface UserService {
    void register(UserDto userDto) throws UserAlreadyRegisterException;
    void save(User user);
    User findByEmail(String email) throws UserNotFoundException;
}