package com.notorify.notorifyapi.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserResponse {
    private String email;
    private String password;
}
