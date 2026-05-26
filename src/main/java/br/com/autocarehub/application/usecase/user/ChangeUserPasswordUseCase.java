package br.com.autocarehub.application.usecase.user;

import br.com.autocarehub.application.ApplicationException;
import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.UserRepository;
import br.com.autocarehub.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public class ChangeUserPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangeUserPasswordUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void execute(Command command) {
        User current =
                userRepository
                        .findById(command.userId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (command.currentPasswordRequired()
                && !passwordEncoder.matches(command.currentPassword(), current.passwordHash())) {
            throw new ApplicationException("Current password is invalid");
        }
        User updated =
                new User(
                        current.id(),
                        current.username(),
                        passwordEncoder.encode(command.newPassword()),
                        current.role(),
                        current.customerId(),
                        current.fullName(),
                        current.profileType(),
                        current.companyName(),
                        current.companyType(),
                        current.employeeSubRole(),
                        current.permissions(),
                        current.active(),
                        current.createdAt());
        userRepository.save(updated);
    }

    public record Command(
            UUID userId,
            String currentPassword,
            String newPassword,
            boolean currentPasswordRequired) {
    }
}
