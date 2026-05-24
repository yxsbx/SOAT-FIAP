package br.com.autocarehub.application.usecase.user;

import br.com.autocarehub.application.ApplicationException;
import br.com.autocarehub.application.repository.UserRepository;
import br.com.autocarehub.domain.User;
import br.com.autocarehub.domain.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

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
                .ifPresent(user -> {
                    throw new ApplicationException("Username already exists");
                });
        User user =
                new User(
                        UUID.randomUUID(),
                        command.username(),
                        passwordEncoder.encode(command.password()),
                        UserRole.valueOf(command.role()),
                        command.customerId(),
                        command.fullName(),
                        command.profileType(),
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
            String employeeSubRole,
            java.util.List<String> permissions,
            boolean active) {
    }
}
