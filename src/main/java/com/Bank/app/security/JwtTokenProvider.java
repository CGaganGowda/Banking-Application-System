package com.Bank.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;


@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    public Long jwtExpiration;

    public String generateToken(Authentication authentication){
        String username = authentication.getName();
        Date current = new Date();
        Date expireDate = new Date(current.getTime()+jwtExpiration);
        String token =  Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(keys())
                .compact();
        return token;
    }

    private Key keys(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    //Username from Token(Subject - from the claims - Token)
    public String getUsernameFromToken(String token){
        Claims claims =  Jwts.parser()
                .verifyWith((SecretKey) keys())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username = claims.getSubject();
        return username;
    }

    //Validate Token

    public Boolean validateToken(String token){
        Jwts.parser()
                .verifyWith((SecretKey) keys())
                .build()
                .parse(token);
        return true;
    }
}
