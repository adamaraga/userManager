package com.backend.userManager.controller;

import com.backend.userManager.dto.HttpResponse;
import com.backend.userManager.dto.UserDto;
import com.backend.userManager.entity.User;
import com.backend.userManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<HttpResponse> signUp(@RequestBody UserDto userDto){
        User newUser = userService.signup(userDto);

        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", newUser))
                .message("User created Successfully")
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .build());
    }
    @PostMapping("/signin")
    public ResponseEntity<HttpResponse> signIn(@RequestBody UserDto UserDto){
        return ResponseEntity.ok().body(userService.signin(UserDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<HttpResponse> refreshToken(@RequestBody UserDto UserDto){
        return ResponseEntity.ok().body(userService.refreshToken(UserDto.getToken()));
    }

    @GetMapping("/activate")
    public ResponseEntity<HttpResponse> activateAccount(@RequestParam("token") String token) {
        Boolean isSuccess = userService.activateAccount(token);

        return ResponseEntity.ok().body( HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("Success", isSuccess))
                .message("Account Activated")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }
}
