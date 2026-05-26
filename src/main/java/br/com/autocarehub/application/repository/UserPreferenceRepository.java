package br.com.autocarehub.application.repository;

import java.util.Optional;
import java.util.UUID;

public interface UserPreferenceRepository {

  Optional<String> findValue(UUID userId, String key);

  String saveValue(UUID userId, String key, String valueJson);
}
