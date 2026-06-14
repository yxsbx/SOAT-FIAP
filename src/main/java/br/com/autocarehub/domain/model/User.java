package br.com.autocarehub.domain.model;

import br.com.autocarehub.domain.enums.UserRole;
import br.com.autocarehub.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record User(
    UUID id,
    String username,
    String passwordHash,
    UserRole role,
    UUID customerId,
    String fullName,
    String profileType,
    String companyName,
    String companyType,
    String employeeSubRole,
    List<String> permissions,
    boolean active,
    LocalDateTime createdAt) {

  public User {
    Objects.requireNonNull(id, "id is required");
    username = requireText(username, "Username is required");
    passwordHash = requireText(passwordHash, "Password hash is required");
    Objects.requireNonNull(role, "role is required");
    fullName = normalizeOptional(fullName, username);
    profileType = normalizeOptional(profileType, role.name());
    companyName = normalizeOptional(companyName, "");
    companyType = normalizeOptional(companyType, "");
    employeeSubRole = normalizeOptional(employeeSubRole, "");
    permissions = List.copyOf(permissions);
    Objects.requireNonNull(createdAt, "createdAt is required");
  }

  public User(
      UUID id,
      String username,
      String passwordHash,
      UserRole role,
      UUID customerId,
      boolean active,
      LocalDateTime createdAt) {
    this(
        id,
        username,
        passwordHash,
        role,
        customerId,
        username,
        role.name(),
        "",
        "",
        "",
        List.of(),
        active,
        createdAt);
  }

  private static String requireText(String value, String message) {
    if (value.isBlank()) {
      throw new DomainException(message);
    }
    return value.trim();
  }

  private static String normalizeOptional(String value, String fallback) {
    if (value.isBlank()) {
      return fallback;
    }
    return value.trim();
  }
}
