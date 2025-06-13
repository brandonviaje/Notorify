package com.notorify.notorifyapi.Service;

import com.notorify.notorifyapi.DTO.LoginUserResponse;
import com.notorify.notorifyapi.DTO.RegisterUserResponse;
import com.notorify.notorifyapi.DTO.VerifyUserResponse;
import com.notorify.notorifyapi.Model.User;
import com.notorify.notorifyapi.Repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User signup(RegisterUserResponse response) {
        User user = User.builder()
                .username(response.getUsername())
                .email(response.getEmail())
                .password(passwordEncoder.encode(response.getPassword()))
                .build();

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(30));
        user.setEnabled(true);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    @Override
    public User authenticate(LoginUserResponse response) {
        User user = userRepository.findByEmail(response.getEmail())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        if(!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify account.");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(response.getEmail(), response.getPassword()));
        return user;
    }

    @Override
    public void verifyUser(VerifyUserResponse response) {
        Optional<User> optionalUser = userRepository.findByEmail(response.getEmail());
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code expired. Please verify account.");
            }
            if(user.getVerificationCode().equals(response.getVerificationCode())) {
                user.setEnabled(true);
                //set verification code to null after verifiying account
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user); //save to DB
            }else{
                throw new RuntimeException("Invalid verification code. Please verify account.");
            }
        }else{
            throw new RuntimeException("User Not Found.");
        }
    }

    @Override
    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to Notorify!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
