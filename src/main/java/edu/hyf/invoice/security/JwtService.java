package edu.hyf.invoice.security;

import edu.hyf.invoice.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service

public class JwtService {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret); // convert the secret key into binary bytes (h -> 104, [104, 233, 105)
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {

        Instant now = Instant.now();

        return Jwts.builder() // start building a token
                .subject(user.getEmail()) // subject - to identify a user
                .claims(Map.of("UserId", user.getId(), // add additional information (id, role) to the token
                        "role", user.getRole().name()))
                .issuedAt(Date.from(now)) // add the issue date
                .expiration(Date.from(now.plusMillis(expirationMs))) // add the expiration date
                .signWith(key) // sign-in with the secret key
                .compact(); // convert everything to final jwt token
    }

    public String extractUserName(String token) {
        return extractClaims(token).getSubject();
    }

    // Check if the token is expired
    public boolean isTokenExpired(String token) {
        Date expiration = extractClaims(token).getExpiration();

        return expiration.before(new Date()); // to check if the jwt expiration date is earlier than the current date.
    }


    // Check if the token is valid
    public boolean isTokenValid(String token, UserDetails userDetails) {
            try {
                String username = extractUserName(token);

                return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            } catch (JwtException | IllegalArgumentException ex) {
                return false;
            }
    }


    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // create a parser, verify with key
        // use a builder(create a builder object)
        // read the token, validate it and prepare for the next method
        // getPayload to get the parsed Claims
    }


}
