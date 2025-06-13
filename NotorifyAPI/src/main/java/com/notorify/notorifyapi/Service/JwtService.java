package com.notorify.notorifyapi.Service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    public String extractUserName(String token);
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver);
    public String generateToken(UserDetails userDetails);
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);
    public long getExpirationTime();
    public boolean isTokenValid(String token, UserDetails userDetails);
}
