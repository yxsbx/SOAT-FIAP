package br.com.autocarehub.interfaces.rest.controller;

import br.com.autocarehub.application.usecase.user.ChangeUserPasswordUseCase;
import br.com.autocarehub.application.usecase.user.CreateUserUseCase;
import br.com.autocarehub.application.usecase.user.GetUserPreferenceUseCase;
import br.com.autocarehub.application.usecase.user.GetUserUseCase;
import br.com.autocarehub.application.usecase.user.ListUsersUseCase;
import br.com.autocarehub.application.usecase.user.SaveUserPreferenceUseCase;
import br.com.autocarehub.application.usecase.user.UpdateUserUseCase;
import br.com.autocarehub.domain.model.User;
import br.com.autocarehub.infrastructure.security.AuthenticatedUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    private static final String HOME_KEY = "home";
    private static final String DEFAULT_HOME_PREFERENCE =
            "{\"widgets\":[\"orders-progress\",\"services-catalog\",\"active-customers\",\"vehicles-in-service\",\"pending-budgets\",\"waiting-contact\",\"ready-pickup\"],\"showAlertsOnHome\":false}";

    private final GetUserUseCase getUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final ChangeUserPasswordUseCase changeUserPasswordUseCase;
    private final GetUserPreferenceUseCase getUserPreferenceUseCase;
    private final SaveUserPreferenceUseCase saveUserPreferenceUseCase;
    private final ObjectMapper objectMapper;

    public UsersController(
            GetUserUseCase getUserUseCase,
            ListUsersUseCase listUsersUseCase,
            CreateUserUseCase createUserUseCase,
            UpdateUserUseCase updateUserUseCase,
            ChangeUserPasswordUseCase changeUserPasswordUseCase,
            GetUserPreferenceUseCase getUserPreferenceUseCase,
            SaveUserPreferenceUseCase saveUserPreferenceUseCase,
            ObjectMapper objectMapper) {
        this.getUserUseCase = getUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.changeUserPasswordUseCase = changeUserPasswordUseCase;
        this.getUserPreferenceUseCase = getUserPreferenceUseCase;
        this.saveUserPreferenceUseCase = saveUserPreferenceUseCase;
        this.objectMapper = objectMapper;
    }

    private static UserResponse toResponse(User user) {
        assert user.customerId() != null;
        return new UserResponse(
                user.id(),
                user.username(),
                user.role().name(),
                user.customerId(),
                user.fullName(),
                user.profileType(),
                user.companyName(),
                user.companyType(),
                user.employeeSubRole(),
                user.permissions(),
                user.active());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(toResponse(getUserUseCase.execute(user.id())));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody UpdateCurrentUserRequest request) {
        User current = getUserUseCase.execute(authenticatedUser.id());
        User updated = updateUserUseCase.execute(new UpdateUserUseCase.Command(
                current.id(),
                current.username(),
                current.role().name(),
                current.customerId(),
                request.fullName(),
                current.profileType(),
                current.companyName(),
                current.companyType(),
                current.employeeSubRole(),
                current.permissions(),
                current.active()));
        return ResponseEntity.ok(toResponse(updated));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changeCurrentPassword(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody ChangePasswordRequest request) {
        changeUserPasswordUseCase.execute(new ChangeUserPasswordUseCase.Command(
                authenticatedUser.id(), request.currentPassword(), request.newPassword(), true));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/preferences/home")
    public ResponseEntity<HomePreferenceResponse> getHomePreference(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ResponseEntity.ok(toHomePreference(
                getUserPreferenceUseCase.execute(authenticatedUser.id(), HOME_KEY, DEFAULT_HOME_PREFERENCE)));
    }

    @PutMapping("/me/preferences/home")
    public ResponseEntity<HomePreferenceResponse> saveHomePreference(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody HomePreferenceRequest request) {
        return ResponseEntity.ok(
                toHomePreference(saveUserPreferenceUseCase.execute(authenticatedUser.id(), HOME_KEY, toJson(request))));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserListResponse> listUsers(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String profileType,
            @RequestParam(required = false) String search) {
        List<UserResponse> items =
                listUsersUseCase.execute(new ListUsersUseCase.Query(active, role, profileType, search)).stream()
                        .map(UsersController::toResponse)
                        .toList();
        return ResponseEntity.ok(new UserListResponse(items));
    }

    @GetMapping("/partners")
    public ResponseEntity<UserListResponse> listPartners() {
        List<UserResponse> items =
                listUsersUseCase.execute(new ListUsersUseCase.Query(true, "ADMIN", null, null)).stream()
                        .filter(user -> user.profileType().equals("WORKSHOP_ADMIN")
                                || user.profileType().equals("PARTS_STORE_ADMIN"))
                        .map(UsersController::toResponse)
                        .toList();
        return ResponseEntity.ok(new UserListResponse(items));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = createUserUseCase.execute(new CreateUserUseCase.Command(
                request.username(),
                request.password(),
                request.role(),
                request.customerId(),
                request.fullName(),
                request.profileType(),
                request.companyName(),
                request.companyType(),
                request.employeeSubRole(),
                request.permissions(),
                request.active()));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId, @Valid @RequestBody UpdateUserRequest request) {
        User user = updateUserUseCase.execute(new UpdateUserUseCase.Command(
                userId,
                request.username(),
                request.role(),
                request.customerId(),
                request.fullName(),
                request.profileType(),
                request.companyName(),
                request.companyType(),
                request.employeeSubRole(),
                request.permissions(),
                request.active()));
        return ResponseEntity.ok(toResponse(user));
    }

    @PatchMapping("/{userId}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resetUserPassword(
            @PathVariable UUID userId, @Valid @RequestBody ResetPasswordRequest request) {
        changeUserPasswordUseCase.execute(
                new ChangeUserPasswordUseCase.Command(userId, null, request.newPassword(), false));
        return ResponseEntity.noContent().build();
    }

    private HomePreferenceResponse toHomePreference(String valueJson) {
        try {
            JsonNode node = objectMapper.readTree(valueJson);
            List<String> widgets = objectMapper.convertValue(
                    node.path("widgets"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            return new HomePreferenceResponse(
                    widgets, node.path("showAlertsOnHome").asBoolean(false));
        } catch (JsonProcessingException e) {
            return toHomePreference(DEFAULT_HOME_PREFERENCE);
        }
    }

    private String toJson(HomePreferenceRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid preference payload", e);
        }
    }

    public record UserResponse(
            UUID id,
            String username,
            String role,
            UUID customerId,
            String fullName,
            String profileType,
            String companyName,
            String companyType,
            String employeeSubRole,
            List<String> permissions,
            boolean active) {}

    public record UserListResponse(List<UserResponse> items) {}

    public record CreateUserRequest(
            @Email @NotBlank String username,
            @Size(min = 8, max = 72) String password,
            @NotBlank @Size(max = 30) String role,
            UUID customerId,
            @NotBlank @Size(max = 120) String fullName,
            @NotBlank @Size(max = 40) String profileType,
            @Size(max = 120) String companyName,
            @Size(max = 30) String companyType,
            @Size(max = 40) String employeeSubRole,
            @Size(max = 20) List<@Pattern(regexp = "^[A-Z_]{3,40}$") String> permissions,
            boolean active) {}

    public record UpdateUserRequest(
            @Email @NotBlank String username,
            @NotBlank @Size(max = 30) String role,
            UUID customerId,
            @NotBlank @Size(max = 120) String fullName,
            @NotBlank @Size(max = 40) String profileType,
            @Size(max = 120) String companyName,
            @Size(max = 30) String companyType,
            @Size(max = 40) String employeeSubRole,
            @Size(max = 20) List<@Pattern(regexp = "^[A-Z_]{3,40}$") String> permissions,
            boolean active) {}

    public record UpdateCurrentUserRequest(@NotBlank @Size(max = 120) String fullName) {}

    public record ChangePasswordRequest(
            @NotBlank @Size(max = 72) String currentPassword, @Size(min = 8, max = 72) String newPassword) {}

    public record ResetPasswordRequest(@Size(min = 8, max = 72) String newPassword) {}

    public record HomePreferenceRequest(
            @NotEmpty @Size(max = 30) List<@Pattern(regexp = "^[a-z0-9-]{3,60}$") String> widgets,
            boolean showAlertsOnHome) {}

    public record HomePreferenceResponse(List<String> widgets, boolean showAlertsOnHome) {}
}
