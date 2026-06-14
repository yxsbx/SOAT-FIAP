package br.com.autocarehub.application.usecase.user;

import br.com.autocarehub.application.exception.ApplicationException;
import br.com.autocarehub.application.port.out.UserRepository;
import br.com.autocarehub.domain.model.User;
import br.com.autocarehub.domain.enums.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;

import static java.util.Objects.requireNonNull;

public class CreateUserUseCase {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public CreateUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User execute(Command command) {
    userRepository
        .findByUsername(command.username())
        .ifPresent(
            user -> {
              throw new ApplicationException("Username already exists");
            });
    User user =
        new User(
            UUID.randomUUID(),
            command.username(),
            requireNonNull(passwordEncoder.encode(command.password())),
            UserRole.valueOf(command.role()),
            command.customerId(),
            command.fullName(),
            command.profileType(),
            command.companyName(),
            command.companyType(),
            command.employeeSubRole(),
            command.permissions(),
            command.active(),
            LocalDateTime.now());
    return userRepository.save(user);
  }

  public record Command(
      String username,
      String password,
      String role,
      UUID customerId,
      String fullName,
      String profileType,
      String companyName,
      String companyType,
      String employeeSubRole,
      java.util.List<String> permissions,
      boolean active) {}
}
