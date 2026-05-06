package br.com.autocarehub.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final SecretKey secretKey;
  private final long expirationSeconds;

  public JwtService(
      @Value("${security.jwt.secret:change-me-change-me-change-me-change-me}") String secret,
      @Value("${security.jwt.expiration-minutes:60}") long expirationMinutes) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationSeconds = expirationMinutes * 60;
  }

  public IssuedToken generateToken(AuthenticatedUser user) {
    Instant issuedAt = Instant.now();
    Instant expiresAt = issuedAt.plusSeconds(expirationSeconds);
    String token =
        Jwts.builder()
            .subject(user.getUsername())
            .claim("userId", user.id().toString())
            .claim("role", user.role())
            .claim("customerId", user.customerId() == null ? null : user.customerId().toString())
            .issuedAt(Date.from(issuedAt))
            .expiration(Date.from(expiresAt))
            .signWith(secretKey)
            .compact();
    return new IssuedToken(token, "Bearer", expirationSeconds);
  }

  public String extractUsername(String token) {
    return extractClaims(token).getSubject();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    return userDetails.getUsername().equals(extractUsername(token))
        && extractClaims(token).getExpiration().after(new Date());
  }

  public Claims extractClaims(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }

  public record IssuedToken(String accessToken, String tokenType, long expiresIn) {}
}
