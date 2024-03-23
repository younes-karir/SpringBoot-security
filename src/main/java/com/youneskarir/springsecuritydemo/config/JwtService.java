package com.youneskarir.springsecuritydemo.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    
    private static final String SECRET = "763151654a285942553b587d5f5753445b2f66426b285748414b575149";
        
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
    
    public String generateToken(Map<String, Object>  extraClaim, UserDetails details){
        return Jwts
                .builder()
                .claims(extraClaim)
                .subject(details.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignedKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String generateToken(UserDetails details){
        return generateToken(new HashMap<>() ,details);
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
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
                return Keys.hmacShaKeyFor(keyBytes);
    }

}
