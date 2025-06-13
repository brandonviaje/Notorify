package com.notorify.notorifyapi.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserRequest {
    private String email;
    private String password;
    private String username;
}
