package com.notorify.notorifyapi.Service;

import com.notorify.notorifyapi.DTO.LoginUserRequest;
import com.notorify.notorifyapi.DTO.RegisterUserRequest;
import com.notorify.notorifyapi.DTO.VerifyUserRequest;
import com.notorify.notorifyapi.Model.User;

public interface AuthenticationService {
    public User signup(RegisterUserRequest response);
    public User authenticate(LoginUserRequest response);
    public void verifyUser(VerifyUserRequest response);
    public void resendVerificationCode(String email);
}
