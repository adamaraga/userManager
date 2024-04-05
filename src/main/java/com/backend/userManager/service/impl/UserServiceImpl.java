package com.backend.userManager.service.impl;

import com.backend.userManager.dto.HttpResponse;
import com.backend.userManager.dto.UserDto;
import com.backend.userManager.entity.Confirmation;
import com.backend.userManager.entity.User;
import com.backend.userManager.repository.ConfirmationRepository;
import com.backend.userManager.repository.UserRepository;
import com.backend.userManager.service.EmailService;
import com.backend.userManager.service.UserService;
import com.backend.userManager.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ConfirmationRepository confirmationRepository;
    private final EmailService emailService;

    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public User signup(UserDto userDto) {
        System.out.println("User :" + userDto.toString());

            if (userRepository.existsByEmail(userDto.getEmail())) { throw new RuntimeException("Email already exists"); }
            User user = new User();
            user.setEmail(userDto.getEmail());
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
        if (userDto.getPassword().length() < 5) { throw new RuntimeException("Password is invalid"); }
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            if(userDto.getRole() == null || !userDto.getRole().equals("ADMIN") ) {
                user.setRole("USER");
            } else {
                user.setRole(userDto.getRole());
            }
            user.setActivated(false);

            User userResult = userRepository.save(user);
                Confirmation confirmation = new Confirmation(user);
                confirmationRepository.save(confirmation);

                //emailService.sendSimpleMailMessage(user.getName(), user.getEmail(), confirmation.getToken());
                //emailService.sendMimeMessageWithAttachments(user.getName(), user.getEmail(), confirmation.getToken());
                //emailService.sendMimeMessageWithEmbeddedFiles(user.getName(), user.getEmail(), confirmation.getToken());
                //emailService.sendHtmlEmailWithEmbeddedFiles(user.getName(), user.getEmail(), confirmation.getToken());
                emailService.sendHtmlEmail(userResult.getFirstName(), userResult.getEmail(), confirmation.getToken());

                return userResult;
    }

    @Override
    public HttpResponse signin(UserDto userDto) {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getEmail(),userDto.getPassword()));
            var user = userRepository.findByEmail(userDto.getEmail()).orElseThrow();

            if (!user.isActivated()) { throw new RuntimeException("Account not active, Validate Email to activate"); }

            var accessToken = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            return HttpResponse.builder()
                    .timeStamp(LocalDateTime.now().toString())
                    .data(Map.of("user", user))
                    .message("User Signed in Successfully")
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .expirationTime("24hrs")
                    .status(HttpStatus.CREATED)
                    .statusCode(HttpStatus.CREATED.value())
                    .build();

    }

    @Override
    public boolean activateAccount(String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);
        User user = userRepository.findByEmail(confirmation.getUser().getEmail()).orElseThrow();
        user.setActivated(true);
        userRepository.save(user);
        confirmationRepository.delete(confirmation);

        return Boolean.TRUE;
    }

    @Override
    public HttpResponse refreshToken(String token) {

        String ourEmail = jwtUtils.extractUsername(token);
        User user = userRepository.findByEmail(ourEmail).orElseThrow();

        if (!jwtUtils.isTokenValid(token, user)) {throw new RuntimeException("Invalid token");}

        var jwt= jwtUtils.generateToken(user);

        return HttpResponse.builder()
                    .timeStamp(LocalDateTime.now().toString())
                    .data(Map.of("Success", Boolean.TRUE))
                    .message("Successfully Refreshed Token")
                    .token(jwt)
                    .refreshToken(token)
                    .expirationTime("24hrs")
                    .status(HttpStatus.OK)
                    .statusCode(HttpStatus.OK.value())
                    .build();


    }
}