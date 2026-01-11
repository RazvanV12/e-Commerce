package com.personal.ecommerceapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Service
public class JwtService {

    private final Key signingKey;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserId(Claims claims) {
        Object val = claims.get("userId");
        if (val instanceof Integer i) return i.longValue();
        if (val instanceof Long l) return l;
        if (val instanceof String s) return Long.parseLong(s);
        throw new IllegalArgumentException("Invalid userId claim");
    }

    public String getEmail(Claims claims) {
        Object val = claims.get("email");
        return val == null ? null : val.toString();
    }

    public String getRole(Claims claims) {
        Object val = claims.get("role");
        return val == null ? null : val.toString();
    }
}
