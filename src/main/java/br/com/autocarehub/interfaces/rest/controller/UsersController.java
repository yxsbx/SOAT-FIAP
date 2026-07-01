package br.com.autocarehub.interfaces.rest.controller;

import java.util.List;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.autocarehub.application.exception.ApplicationException;
import br.com.autocarehub.application.port.out.CompanyRepository;
import br.com.autocarehub.application.usecase.user.ChangeUserPasswordUseCase;
import br.com.autocarehub.application.usecase.user.CreateUserUseCase;
import br.com.autocarehub.application.usecase.user.GetUserPreferenceUseCase;
import br.com.autocarehub.application.usecase.user.GetUserUseCase;
import br.com.autocarehub.application.usecase.user.ListUsersUseCase;
import br.com.autocarehub.application.usecase.user.SaveUserPreferenceUseCase;
import br.com.autocarehub.application.usecase.user.UpdateUserUseCase;
import br.com.autocarehub.domain.model.Company;
import br.com.autocarehub.domain.model.User;
import br.com.autocarehub.infrastructure.security.AuthenticatedUser;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    private static final String HOME_KEY = "home";
    private static final String DEFAULT_HOME_PREFERENCE =
            """
                    {
                      "widgets": [
                        "orders-progress",
                        "services-catalog",
                        "active-customers",
                        "vehicles-in-service",
                        "pending-budgets",
                        "waiting-contact",
                        "ready-pickup"
                      ],
                      "showAlertsOnHome": false
                    }
                    """;

    private final GetUserUseCase getUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final ChangeUserPasswordUseCase changeUserPasswordUseCase;
    private final GetUserPreferenceUseCase getUserPreferenceUseCase;
    private final SaveUserPreferenceUseCase saveUserPreferenceUseCase;
    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper;

    public UsersController(
            GetUserUseCase getUserUseCase,
            ListUsersUseCase listUsersUseCase,
            CreateUserUseCase createUserUseCase,
            UpdateUserUseCase updateUserUseCase,
            ChangeUserPasswordUseCase changeUserPasswordUseCase,
            GetUserPreferenceUseCase getUserPreferenceUseCase,
            SaveUserPreferenceUseCase saveUserPreferenceUseCase,
            CompanyRepository companyRepository,
            ObjectMapper objectMapper) {
        this.getUserUseCase = getUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.changeUserPasswordUseCase = changeUserPasswordUseCase;
        this.getUserPreferenceUseCase = getUserPreferenceUseCase;
        this.saveUserPreferenceUseCase = saveUserPreferenceUseCase;
        this.companyRepository = companyRepository;
        this.objectMapper = objectMapper;
    }

    private static UserResponse toResponse(User user) {
        return new UserResponse(
                user.id(),
                user.username(),
                user.role().name(),
                user.customerId(),
                user.companyId(),
                user.fullName(),
                user.profileType(),
                user.companyName(),
                user.companyType(),
                user.employeeSubRole(),
                user.permissions(),
                user.active());
    }

    private static CompanyResponse toCompanyResponse(Company company) {
        return new CompanyResponse(company.id(), company.name(), company.type(), company.active());
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
                current.companyId(),
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
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String profileType,
            @RequestParam(required = false) String search) {
        User requester = getUserUseCase.execute(authenticatedUser.id());
        List<UserResponse> items =
                listUsersUseCase.execute(new ListUsersUseCase.Query(active, role, profileType, search)).stream()
                        .filter(user -> canManageUser(requester, user))
                        .map(UsersController::toResponse)
                        .toList();
        return ResponseEntity.ok(new UserListResponse(items));
    }

    @GetMapping("/partners")
    public ResponseEntity<UserListResponse> listPartners() {
        List<UserResponse> items =
                listUsersUseCase.execute(new ListUsersUseCase.Query(true, "ADMIN", null, null)).stream()
                        .filter(user -> "WORKSHOP_ADMIN".equals(user.profileType())
                                || "PARTS_STORE_ADMIN".equals(user.profileType()))
                        .map(UsersController::toResponse)
                        .toList();
        return ResponseEntity.ok(new UserListResponse(items));
    }

    @GetMapping("/companies")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyListResponse> listCompanies(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        User requester = getUserUseCase.execute(authenticatedUser.id());
        List<CompanyResponse> items = companyRepository.findAll().stream()
                .filter(company -> isMasterAdmin(requester) || company.id().equals(requester.companyId()))
                .map(UsersController::toCompanyResponse)
                .toList();
        return ResponseEntity.ok(new CompanyListResponse(items));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody CreateUserRequest request) {
        User requester = getUserUseCase.execute(authenticatedUser.id());
        UserCommandData data = normalizeUserCommand(requester, request.toCommandData(), null);
        User user = createUserUseCase.execute(new CreateUserUseCase.Command(
                data.username(),
                request.password(),
                data.role(),
                data.customerId(),
                data.companyId(),
                data.fullName(),
                data.profileType(),
                data.companyName(),
                data.companyType(),
                data.employeeSubRole(),
                data.permissions(),
                data.active()));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request) {
        User requester = getUserUseCase.execute(authenticatedUser.id());
        User current = getUserUseCase.execute(userId);
        UserCommandData data = normalizeUserCommand(requester, request.toCommandData(), current);
        User user = updateUserUseCase.execute(new UpdateUserUseCase.Command(
                userId,
                data.username(),
                data.role(),
                data.customerId(),
                data.companyId(),
                data.fullName(),
                data.profileType(),
                data.companyName(),
                data.companyType(),
                data.employeeSubRole(),
                data.permissions(),
                data.active()));
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

    private UserCommandData normalizeUserCommand(User requester, UserCommandData data, @Nullable User current) {
        if (current != null && !canManageUser(requester, current)) {
            throw new ApplicationException("User is outside the current company scope");
        }
        UserCommandData normalized = normalizeRoleProfile(data);
        if (isMasterAdmin(requester)) {
            return withResolvedCompany(normalized);
        }
        if ("MASTER_ADMIN".equals(normalized.profileType())) {
            throw new ApplicationException("Only master admin can create or update master admin users");
        }
        String expectedProfile =
                "PARTS_STORE".equals(requester.companyType()) ? "PARTS_STORE_EMPLOYEE" : "WORKSHOP_EMPLOYEE";
        String expectedCompanyType = "PARTS_STORE".equals(requester.companyType()) ? "PARTS_STORE" : "WORKSHOP";
        UserCommandData scoped = new UserCommandData(
                normalized.username(),
                "EMPLOYEE",
                normalized.customerId(),
                requester.companyId(),
                normalized.fullName(),
                expectedProfile,
                requester.companyName(),
                expectedCompanyType,
                false,
                normalizeEmployeeSubRole(normalized.employeeSubRole()),
                normalized.permissions(),
                normalized.active());
        if (current != null
                && !canManageUser(
                requester,
                new User(
                        current.id(),
                        scoped.username(),
                        current.passwordHash(),
                        current.role(),
                        scoped.customerId(),
                        scoped.companyId(),
                        scoped.fullName(),
                        scoped.profileType(),
                        scoped.companyName(),
                        scoped.companyType(),
                        scoped.employeeSubRole(),
                        scoped.permissions(),
                        scoped.active(),
                        current.createdAt()))) {
            throw new ApplicationException("User is outside the current company scope");
        }
        return scoped;
    }

    private UserCommandData withResolvedCompany(UserCommandData data) {
        if ("MASTER_ADMIN".equals(data.profileType())) {
            Company platform = companyRepository
                    .findByName("AutoCare Hub")
                    .orElseGet(() -> companyRepository.save(Company.create("AutoCare Hub", "PLATFORM")));
            return data.withCompany(platform);
        }
        if ("CUSTOMER_OWNER".equals(data.profileType())) {
            return data.withoutCompany();
        }
        if (data.companyId() != null && !data.createCompany()) {
            Company company = companyRepository
                    .findById(data.companyId())
                    .orElseThrow(() -> new ApplicationException("Company not found"));
            if (!company.type().equals(data.companyType())) {
                throw new ApplicationException("Company type does not match user profile");
            }
            return data.withCompany(company);
        }
        if (data.companyName().isBlank()) {
            throw new ApplicationException("Company name is required");
        }
        if (!data.createCompany()) {
            Company company = companyRepository
                    .findByName(data.companyName())
                    .orElseThrow(() -> new ApplicationException("Company not found"));
            if (!company.type().equals(data.companyType())) {
                throw new ApplicationException("Company type does not match user profile");
            }
            return data.withCompany(company);
        }
        companyRepository.findByName(data.companyName()).ifPresent(company -> {
            throw new ApplicationException("Company already exists");
        });
        return data.withCompany(companyRepository.save(Company.create(data.companyName(), data.companyType())));
    }

    private UserCommandData normalizeRoleProfile(UserCommandData data) {
        return switch (data.profileType()) {
            case "MASTER_ADMIN" -> data.withRole("ADMIN").withoutCompany().withoutEmployeeSubRole();
            case "WORKSHOP_ADMIN" -> data.withRole("ADMIN").withCompanyType("WORKSHOP").withoutEmployeeSubRole();
            case "PARTS_STORE_ADMIN" -> data.withRole("ADMIN").withCompanyType("PARTS_STORE").withoutEmployeeSubRole();
            case "WORKSHOP_EMPLOYEE" -> data.withRole("EMPLOYEE")
                    .withCompanyType("WORKSHOP")
                    .withEmployeeSubRole(normalizeEmployeeSubRole(data.employeeSubRole()));
            case "PARTS_STORE_EMPLOYEE" -> data.withRole("EMPLOYEE")
                    .withCompanyType("PARTS_STORE")
                    .withEmployeeSubRole(normalizeEmployeeSubRole(data.employeeSubRole()));
            case "CUSTOMER_OWNER" -> data.withRole("CUSTOMER").withoutCompany().withoutEmployeeSubRole();
            default -> throw new ApplicationException("Invalid user profile type");
        };
    }

    private boolean canManageUser(User requester, User target) {
        if (isMasterAdmin(requester)) {
            return true;
        }
        if ("MASTER_ADMIN".equals(target.profileType())) {
            return false;
        }
        return requester.companyId() != null && requester.companyId().equals(target.companyId());
    }

    private boolean isMasterAdmin(User user) {
        return "MASTER_ADMIN".equals(user.profileType());
    }

    private String normalizeEmployeeSubRole(String value) {
        return value == null || value.isBlank() ? "UNSPECIFIED" : value;
    }

    public record UserResponse(
            UUID id,
            String username,
            String role,
            @Nullable UUID customerId,
            @Nullable UUID companyId,
            String fullName,
            String profileType,
            String companyName,
            String companyType,
            String employeeSubRole,
            List<String> permissions,
            boolean active) {
    }

    public record UserListResponse(List<UserResponse> items) {
    }

    public record CompanyResponse(UUID id, String name, String type, boolean active) {
    }

    public record CompanyListResponse(List<CompanyResponse> items) {
    }

    public record CreateUserRequest(
            @Email @NotBlank String username,
            @Size(min = 8, max = 72) String password,
            @NotBlank @Size(max = 30) String role,
            @Nullable UUID customerId,
            @Nullable UUID companyId,
            @NotBlank @Size(max = 120) String fullName,
            @NotBlank @Size(max = 40) String profileType,
            @Size(max = 120) String companyName,
            @Size(max = 30) String companyType,
            @Nullable Boolean createCompany,
            @Size(max = 40) String employeeSubRole,
            @Size(max = 20) List<@Pattern(regexp = "^[A-Z_]{3,40}$") String> permissions,
            boolean active) {

        UserCommandData toCommandData() {
            return new UserCommandData(
                    username,
                    role,
                    customerId,
                    companyId,
                    fullName,
                    profileType,
                    companyName == null ? "" : companyName,
                    companyType == null ? "" : companyType,
                    Boolean.TRUE.equals(createCompany),
                    employeeSubRole == null ? "" : employeeSubRole,
                    permissions == null ? List.of() : permissions,
                    active);
        }
    }

    public record UpdateUserRequest(
            @Email @NotBlank String username,
            @NotBlank @Size(max = 30) String role,
            @Nullable UUID customerId,
            @Nullable UUID companyId,
            @NotBlank @Size(max = 120) String fullName,
            @NotBlank @Size(max = 40) String profileType,
            @Size(max = 120) String companyName,
            @Size(max = 30) String companyType,
            @Nullable Boolean createCompany,
            @Size(max = 40) String employeeSubRole,
            @Size(max = 20) List<@Pattern(regexp = "^[A-Z_]{3,40}$") String> permissions,
            boolean active) {

        UserCommandData toCommandData() {
            return new UserCommandData(
                    username,
                    role,
                    customerId,
                    companyId,
                    fullName,
                    profileType,
                    companyName == null ? "" : companyName,
                    companyType == null ? "" : companyType,
                    Boolean.TRUE.equals(createCompany),
                    employeeSubRole == null ? "" : employeeSubRole,
                    permissions == null ? List.of() : permissions,
                    active);
        }
    }

    private record UserCommandData(
            String username,
            String role,
            @Nullable UUID customerId,
            @Nullable UUID companyId,
            String fullName,
            String profileType,
            String companyName,
            String companyType,
            boolean createCompany,
            String employeeSubRole,
            List<String> permissions,
            boolean active) {

        UserCommandData withRole(String nextRole) {
            return new UserCommandData(
                    username,
                    nextRole,
                    customerId,
                    companyId,
                    fullName,
                    profileType,
                    companyName,
                    companyType,
                    createCompany,
                    employeeSubRole,
                    permissions,
                    active);
        }

        UserCommandData withCompanyType(String nextCompanyType) {
            return new UserCommandData(
                    username,
                    role,
                    customerId,
                    companyId,
                    fullName,
                    profileType,
                    companyName,
                    nextCompanyType,
                    createCompany,
                    employeeSubRole,
                    permissions,
                    active);
        }

        UserCommandData withEmployeeSubRole(String nextEmployeeSubRole) {
            return new UserCommandData(
                    username,
                    role,
                    customerId,
                    companyId,
                    fullName,
                    profileType,
                    companyName,
                    companyType,
                    createCompany,
                    nextEmployeeSubRole,
                    permissions,
                    active);
        }

        UserCommandData withoutCompany() {
            return new UserCommandData(
                    username,
                    role,
                    customerId,
                    null,
                    fullName,
                    profileType,
                    "",
                    "",
                    false,
                    employeeSubRole,
                    permissions,
                    active);
        }

        UserCommandData withoutEmployeeSubRole() {
            return new UserCommandData(
                    username,
                    role,
                    customerId,
                    companyId,
                    fullName,
                    profileType,
                    companyName,
                    companyType,
                    createCompany,
                    "",
                    permissions,
                    active);
        }

        UserCommandData withCompany(Company company) {
            return new UserCommandData(
                    username,
                    role,
                    customerId,
                    company.id(),
                    fullName,
                    profileType,
                    company.name(),
                    company.type(),
                    false,
                    employeeSubRole,
                    permissions,
                    active);
        }
    }

    public record UpdateCurrentUserRequest(@NotBlank @Size(max = 120) String fullName) {
    }

    public record ChangePasswordRequest(
            @NotBlank @Size(max = 72) String currentPassword, @Size(min = 8, max = 72) String newPassword) {
    }

    public record ResetPasswordRequest(@Size(min = 8, max = 72) String newPassword) {
    }

    public record HomePreferenceRequest(
            @NotEmpty @Size(max = 30) List<@Pattern(regexp = "^[a-z0-9-]{3,60}$") String> widgets,
            boolean showAlertsOnHome) {
    }

    public record HomePreferenceResponse(List<String> widgets, boolean showAlertsOnHome) {
    }
}
