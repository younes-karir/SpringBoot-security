package com.youneskarir.springsecuritydemo.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    
    @Value("${application.security.jwt.secret-key}")
    private String secret ;
    
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration ;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration ;
        
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails details){
        return generateToken(new HashMap<>() ,details);
    }
    
    public String generateToken(Map<String, Object>  extraClaim, UserDetails details){
        return buildToken(extraClaim,details,jwtExpiration);
    }

    public String generateRefreshToken(UserDetails details){
        return buildToken(new HashMap<>(),details,refreshExpiration);
    }
    
    public String buildToken(Map<String, Object>  extraClaim, UserDetails details,long jwtExpiration){
        return Jwts
                .builder()
                .claims(extraClaim)
                .subject(details.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignedKey(), Jwts.SIG.HS256)
                .compact();
    }


    
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && (!isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSignedKey())
                .build().parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignedKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
                return Keys.hmacShaKeyFor(keyBytes);
    }

}
