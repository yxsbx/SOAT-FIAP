package br.com.autocarehub.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.autocarehub.domain.model.User;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    List<User> findAll();
}
