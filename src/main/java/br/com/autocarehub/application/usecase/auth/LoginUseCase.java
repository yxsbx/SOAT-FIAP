package br.com.autocarehub.application.usecase.auth;

import br.com.autocarehub.infrastructure.security.AuthenticatedUser;
import br.com.autocarehub.infrastructure.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static java.util.Objects.requireNonNull;

public class LoginUseCase {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public LoginUseCase(AuthenticationManager authenticationManager, JwtService jwtService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  public Output execute(Command command) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(command.username(), command.password()));
    JwtService.IssuedToken token =
        jwtService.generateToken((AuthenticatedUser) requireNonNull(authentication.getPrincipal()));
    return new Output(token.accessToken(), token.tokenType(), token.expiresIn());
  }

  public record Command(String username, String password) {}

  public record Output(String accessToken, String tokenType, long expiresIn) {}
}
