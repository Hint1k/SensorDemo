package com.test.sensor.jwt;

import java.util.List;

public interface JwtService {

    String generateToken(String username, List<String> roles);

    String extractUsername(String token);

    boolean isTokenExpired(String token);

    List<String> extractRoles(String token);

    String extractTokenFromHeader(String authHeader);
}