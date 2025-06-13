package com.notorify.notorifyapi.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyUserResponse {
    private String email;
    private String verificationCode;
}
