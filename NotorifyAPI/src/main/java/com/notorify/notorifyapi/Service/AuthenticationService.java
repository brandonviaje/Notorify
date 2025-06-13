package com.notorify.notorifyapi.Service;

import com.notorify.notorifyapi.DTO.LoginUserResponse;
import com.notorify.notorifyapi.DTO.RegisterUserResponse;
import com.notorify.notorifyapi.DTO.VerifyUserResponse;
import com.notorify.notorifyapi.Model.User;

public interface AuthenticationService {
    public User signup(RegisterUserResponse response);
    public User authenticate(LoginUserResponse response);
    public void verifyUser(VerifyUserResponse response);
    public void resendVerificationCode(String email);
}
