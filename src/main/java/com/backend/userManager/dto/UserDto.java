package com.backend.userManager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private UUID id;

    private String email;
    private String firstName;
    private String LastName;
    private String token;
    private String password;
    private String role;
    private boolean isActivated;

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", token='" + token + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", isActivated=" + isActivated +
                '}';
    }
}
