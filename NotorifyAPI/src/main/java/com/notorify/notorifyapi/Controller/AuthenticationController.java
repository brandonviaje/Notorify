package com.notorify.notorifyapi.Controller;

import com.notorify.notorifyapi.DTO.LoginResponse;
import com.notorify.notorifyapi.DTO.LoginUserResponse;
import com.notorify.notorifyapi.DTO.RegisterUserResponse;
import com.notorify.notorifyapi.DTO.VerifyUserResponse;
import com.notorify.notorifyapi.Model.User;
import com.notorify.notorifyapi.Service.AuthenticationService;
import com.notorify.notorifyapi.Service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserResponse response){
        User registeredUser = authenticationService.signup(response);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserResponse response){
        User authenticatedUser = authenticationService.authenticate(response);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken,jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserResponse response){
        try{
            authenticationService.verifyUser(response);
            return ResponseEntity.ok("Account verified successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email){
        try{
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent.");
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
