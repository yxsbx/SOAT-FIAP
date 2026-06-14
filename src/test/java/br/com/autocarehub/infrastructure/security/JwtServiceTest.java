package br.com.autocarehub.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.autocarehub.domain.enums.UserRole;
import br.com.autocarehub.domain.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

  private static final String SECRET = "test-jwt-secret-with-at-least-32-bytes";

  @Test
  void shouldRejectMissingSecret() {
    assertThatThrownBy(() -> new JwtService("", 60))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("JWT secret must be provided through security.jwt.secret or JWT_SECRET");
  }

  @Test
  void shouldRejectShortSecret() {
    assertThatThrownBy(() -> new JwtService("short-secret", 60))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("JWT secret must have at least 32 bytes");
  }

  @Test
  void shouldGenerateSignedTokenWithExpiration() {
    JwtService jwtService = new JwtService(SECRET, 60);
    AuthenticatedUser user =
        new AuthenticatedUser(
            new User(
                UUID.randomUUID(),
                "admin@autocarehub.com",
                "$2a$10$hashhashhashhashhashhashhashhashhashhashhashhashhash",
                UserRole.ADMIN,
                null,
                "Admin",
                "WORKSHOP_ADMIN",
                "Oficina",
                "WORKSHOP",
                "",
                List.of(),
                true,
                LocalDateTime.now()));

    JwtService.IssuedToken token = jwtService.generateToken(user);

    assertThat(token.tokenType()).isEqualTo("Bearer");
    assertThat(token.expiresIn()).isEqualTo(3600);
    assertThat(jwtService.extractUsername(token.accessToken())).isEqualTo(user.getUsername());
    assertThat(jwtService.isTokenValid(token.accessToken(), user)).isTrue();
  }
}
