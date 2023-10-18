package com.wild.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.wild.security.dto.UserDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtilities {

    private Key getKey() {
    byte[] keyBytes = Decoders.BASE64.decode("SECRETKEYHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorld");
    // La clé doit faire 256bits
    Key key = Keys.hmacShaKeyFor(keyBytes);
    return key;
    }

    private String createToken(Map<String, Object> claims, String subject) {
    return Jwts
        .builder() // instancie la création du token
        .setClaims(claims) // ajoute l'objet avec les infos (ex:role)
        .setSubject(subject) // ajoute id (username ou email)
        .setIssuedAt(new Date(System.currentTimeMillis())) // date de création du token
        .setExpiration(new Date(System.currentTimeMillis() +
            1000 * 60 * 60 * 10)) // date d'expiration 
        .signWith(getKey(), SignatureAlgorithm.HS256) // signe le token utilisant un algorithme de cryptographie
        //getKey ajoute une clé secrète permettant de vérifier que le token n'a pas été modifié
        .compact(); // renvoi string hashé
    }

    public String generateToken(UserDto userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", userDetails.getRoles());
    return createToken(claims, userDetails.getUsername());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Boolean validateToken(String token,
        UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) &&
            !isTokenExpired(token));
    }

}