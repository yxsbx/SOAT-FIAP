package br.com.autocarehub.application.usecase.user;

import br.com.autocarehub.application.port.out.UserRepository;
import br.com.autocarehub.domain.model.User;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ListUsersUseCase {

    private final UserRepository userRepository;

    public ListUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> execute(Query query) {
        return userRepository.findAll().stream()
                .filter(user -> query == null || query.active() == null || user.active() == query.active())
                .filter(user -> query == null
                        || query.role() == null
                        || user.role().name().equals(query.role()))
                .filter(user -> query == null
                        || query.profileType() == null
                        || user.profileType().equals(query.profileType()))
                .filter(user -> matchesSearch(user, query == null ? null : query.search()))
                .sorted(Comparator.comparing(User::fullName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private boolean matchesSearch(User user, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        String value = search.toLowerCase(Locale.ROOT);
        return user.fullName().toLowerCase(Locale.ROOT).contains(value)
                || user.username().toLowerCase(Locale.ROOT).contains(value)
                || user.profileType().toLowerCase(Locale.ROOT).contains(value)
                || user.employeeSubRole().toLowerCase(Locale.ROOT).contains(value);
    }

    public record Query(Boolean active, String role, String profileType, String search) {}
}
