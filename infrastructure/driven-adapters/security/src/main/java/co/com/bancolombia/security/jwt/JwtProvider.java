package co.com.bancolombia.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

  public static final String CLAIM_ROLE = "roles";

  @Value("${jwt.secret}")
  private String secret;
  @Value("${jwt.expiration}")
  private Integer expiration;

  public String generateToken(UserDetails userDetails) {
    return Jwts.builder()
        .subject(userDetails.getUsername())
        .claim(CLAIM_ROLE, userDetails.getAuthorities())
        .issuedAt(new Date())
        .expiration(new Date(new Date().getTime() + expiration))
        .signWith(getKey(secret))
        .compact();
  }

  private SecretKey getKey(String secret) {
    byte[] secretBytes = Decoders.BASE64URL.decode(secret);
    return Keys.hmacShaKeyFor(secretBytes);
  }
}
