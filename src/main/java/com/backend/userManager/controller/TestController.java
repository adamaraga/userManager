package com.backend.userManager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class TestController {
    @GetMapping("/test")
    public ResponseEntity<String> getTest() {
        return ResponseEntity.ok().body( "Protected Route");
    }
}
