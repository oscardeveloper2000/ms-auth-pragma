package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.user.User;

/**
 * Provides authentication-related operations.
 */
public interface AuthGateway {

  /**
   * Checks if the raw password matches the encoded password.
   *
   * @param rawPassword     the plain text password
   * @param encodedPassword the encoded password
   * @return true if the passwords match, false otherwise
   */
  boolean matches(String rawPassword, String encodedPassword);

  /**
   * Encodes a raw password.
   *
   * @param rawPassword the plain text password
   * @return the encoded password
   */
  String encode(String rawPassword);

  /**
   * Generates an authentication token for the specified user.
   *
   * @param user the user for whom the token is generated
   * @return the generated token
   */
  String generateToken(User user);
}
