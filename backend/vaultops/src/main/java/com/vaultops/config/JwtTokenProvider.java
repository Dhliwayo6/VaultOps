package com.vaultops.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:default-secret-key-that-is-very-long-and-secure-for-vaultops-2026}")
    private String jwtSecret;

    private static final long ACCESS_TOKEN_EXPIRY = 15 * 60 * 1000; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60 * 1000; // 7 days

    public String generateAccessToken(String email, String role, String name, Long userId) {
        return JWT.create()
                .withSubject(email)
                .withClaim("role", role)
                .withClaim("name", name)
                .withClaim("userId", userId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public String generateRefreshToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withClaim("jti", java.util.UUID.randomUUID().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtSecret)).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return JWT.decode(token).getSubject();
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        String email = decodedJWT.getSubject();
        String role = decodedJWT.getClaim("role").asString();

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
        return new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));
    }
}
