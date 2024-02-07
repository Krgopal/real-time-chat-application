package com.demo.service;

public interface JwtService {
    String generateToken(String userName);
    Boolean validateToken(String token);
    String extractUsername(String token);
    String encodePassword(String password);
}
