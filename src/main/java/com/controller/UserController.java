package com.controller;


import com.dto.UserDto;
import com.exception.UserAlreadyRegisterException;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
public class UserController {
    private static final String USER_ALREADY_REGISTER_EXCEPTION_OR_INVALID_DATA = "user exists";
    @Autowired
    private UserService userService;

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody UserDto userDto) {
        if (userService.register(userDto)) {
            return;
        }
        throw new UserAlreadyRegisterException(HttpStatus.BAD_REQUEST, USER_ALREADY_REGISTER_EXCEPTION_OR_INVALID_DATA);
    }
}
