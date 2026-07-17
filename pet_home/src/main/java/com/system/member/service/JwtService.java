package com.system.member.service;

import com.system.member.entity.UserBean;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiresIn;
    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiresIn;
    private final SecureRandom secureRandom = new SecureRandom();

    private SecretKey getKey(){
        byte[] key = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(key);
    }

    public Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token){
        Claims claims = getClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    public Integer getTokenVersion(String token){
        Claims claims = getClaims(token);
        return claims.get("tokenVersion", Integer.class);
    }

    public Integer getPermissionVersion(String token){
        Claims claims = getClaims(token);
        return claims.get("perMissionVersion", Integer.class);
    }

    public String  getLoginChallengeToken(Long userId){
        Date now = new Date();
        Date expire = new Date(now.getTime() + 1000 * 60 * 5);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "LOGIN_CHALLENGE")
                .issuedAt(now)
                .expiration(expire)
                .signWith(getKey())
                .compact();
    }

    public String getAccessToken(UserBean user){
        Date now = new Date();
        Date expireDate = new Date((now.getTime()+accessTokenExpiresIn*1000));


        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("tokenVersion", user.getTokenVersion())
                .claim("perMissionVersion", user.getPermissionVersion())
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(getKey())
                .compact();
    }

    public String generateSecureToken(){
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public String hashSecureToken(String refreshToken){
        try {
            MessageDigest message = MessageDigest.getInstance("SHA-256");
            byte[] hash = message.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        }catch (NoSuchAlgorithmException e){
            throw new RuntimeException("secureToken hashing failed");
        }
    }

    public LocalDateTime getAccessTokenExpireAt(){
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(accessTokenExpiresIn);
    }

    public LocalDateTime getSecureTokenExpireAt(){
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(refreshTokenExpiresIn);
    }
}
