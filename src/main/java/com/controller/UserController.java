package com.controller;


import com.dto.UserDto;
import com.exception.UserAlreadyRegisterException;
import com.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
public class UserController {
    @Autowired
    private UserServiceImpl userServiceImpl;

    @PostMapping("register")
    public ResponseEntity register(@RequestBody UserDto userDto) throws UserAlreadyRegisterException {
       return userServiceImpl.register(userDto);
    }
}