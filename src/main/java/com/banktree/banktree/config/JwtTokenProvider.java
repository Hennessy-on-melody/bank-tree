package com.banktree.banktree.config;


import com.banktree.banktree.domain.form.TokenInfo;
import com.banktree.banktree.exception.ErrorCode;
import com.banktree.banktree.exception.UsersException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static com.banktree.banktree.exception.ErrorCode.*;

@Component
@Slf4j
public class JwtTokenProvider {
    private final Key key;
    private final long TOKEN_EXPIRED_TIME;
    private final long REFRESH_TOKEN_EXPIRED_TIME;
    private final static String AUTH = "auth";
    private final static String BEARER_TYPE = "Bearer";


    public JwtTokenProvider(
            @Value("${jwt.access.secret}") String secretKey,
            @Value("${jwt.access.token-validity-time}") long expiredTime,
            @Value("${jwt.refresh.secret}") String refreshKey,
            @Value("${jwt.refresh.token-validity-time}") long refreshExpiredTime) {

        byte[] secretByte = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(secretByte);
        this.TOKEN_EXPIRED_TIME = expiredTime;
        this.REFRESH_TOKEN_EXPIRED_TIME = refreshExpiredTime;
    }

    public TokenInfo generateToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = Instant.now().toEpochMilli();

        Date accessTokenExpiredIn = new Date(now + TOKEN_EXPIRED_TIME);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTH, authorities)
                .setExpiration(accessTokenExpiredIn)
                .signWith(key, SignatureAlgorithm.ES256)
                .compact();
        log.info("token: " + accessToken);

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRED_TIME))
                .signWith(key, SignatureAlgorithm.ES256)
                .compact();

        return TokenInfo.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String token){
        Claims claims = parseClaims(token);
        // Claim 이란 주로 사용자에 대한 프로필 정보나 토큰 자체에 대한 메타 데이터 포함
        if (claims.get(AUTH) == null){ //만약 token 을 parseClaims 하여 키값(AUTH) 에 있는 authorities 를 확인
            log.error("NO AUTHORITIES");
            throw new UsersException(NO_TOKEN_INFORMATION);
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTH).toString().split(",")) // 권한 정보를 Array.stream
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        // stream 한 것을 map 으로 각기 SimpleGrantedAuthority 형 객체 생성

        UserDetails principal = // principal 은 주로 이름, 패스워드, 권한 정보로 구성
                new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private Claims parseClaims(String token){
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
}
