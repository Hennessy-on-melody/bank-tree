package com.banktree.banktree.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.access.secret}") String secretKey;
    @Value("${jwt.access.token-validity-time}") long expiredTime;
    @Value("${jwt.refresh.secret}") String refreshKey;
    @Value("${jwt.refresh.token-validity-time}") long refreshExpiredTime;


}
