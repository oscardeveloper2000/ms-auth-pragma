package co.com.bancolombia.security.adapter;


import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.AuthGateway;
import co.com.bancolombia.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static org.springframework.security.core.userdetails.User.withUsername;

@Component
@RequiredArgsConstructor
public class AuthGatewayImpl implements AuthGateway {

  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;

  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  @Override
  public String encode(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  @Override
  public String generateToken(User user) {
    return jwtProvider.generateToken(withUsername(user.getEmail()).password(user.getPasswordHash())
        .authorities(user.getRoleName())
        .build());
  }
}
