package com.backend.userManager.service;


import com.backend.userManager.dto.HttpResponse;
import com.backend.userManager.dto.UserDto;
import com.backend.userManager.entity.User;

public interface UserService {
    User signup(UserDto userDto);
    HttpResponse signin(UserDto userDto);
    boolean activateAccount(String token);
    HttpResponse refreshToken(String token);
}