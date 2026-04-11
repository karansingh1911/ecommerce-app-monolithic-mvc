package com.karan.ecommerce_app.service;

import com.karan.ecommerce_app.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    // JwtService is responsible for creating the JWT token

    private final String secret;
    private final long jwtExpiration;

    // initializing the Jwt bean with the Jwt
    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long jwtExpiration) {
        this.secret = secret;
        this.jwtExpiration = jwtExpiration;
    }

    // when the JwtService bean is created, SECRET is created!
//    private String generateSecretKey() {
//
//        try {
//            KeyGenerator keygen = KeyGenerator.getInstance("HmacSHA256"); // generator
//            SecretKey secretKey = keygen.generateKey(); //ex:  byte [] =[23,112,12,1223,...]
//            return Base64.getEncoder().encodeToString(secretKey.getEncoded()); //byte[]->base64 string
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Error generating secret key", e);
//        }
//    }

    // this is actually generating the key which will be used to sign the JWT token
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // generating token and signing it with getKeyFromSecret()
    public String generateToken(String email, Role role) {

        // whatever role the user is claiming let it go through for checking
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.name());

        // for consistency- Both timestamps should derive from the same base time
        long now = System.currentTimeMillis();
        long expiry= now+ jwtExpiration;
        Date issuedAt = new Date(now);
        Date  expiration= new Date(expiry);


        return Jwts.builder().claims(claims).subject(email).issuedAt(issuedAt).expiration(expiration).signWith(getSigningKey()).compact();
    }



    // helper methods for extracting email, expiration, all claims, single claim:


    //extract all claims
    public Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith((SecretKey) getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    //extract Claim
    private <T> T extractClaim(String token, Function<Claims,T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    //extract email
    public String extractEmail(String token){
        return extractClaim(token,claims->{
           return claims.getSubject();
        });
    }

    //extract token expiration time
    private Date extractExpiration(String token){
        return extractClaim(token,claims->{
            return claims.getExpiration();
        });
    }

    //extract role if needed
    private Role extractRole(String token){
        String role = extractClaim(token, claims -> claims.get("role",String.class));
        return Role.valueOf(role);
    }

    // check if the token is expired
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    //token validity logic
    public boolean isTokenValid(String token, UserDetails userDetails){
        try{
            final String extractedEmail = extractEmail(token);
            return extractedEmail.equals(userDetails.getUsername()) && !isTokenExpired(token);
        }catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }


}


// enhancements : completed ✅
// 1.@Value - so that token will be valid even after the restart and be kept in env/ properties
