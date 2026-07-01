package br.com.autocarehub.application.usecase.user;

import java.util.UUID;

import br.com.autocarehub.application.port.out.UserPreferenceRepository;

public class SaveUserPreferenceUseCase {

    private final UserPreferenceRepository userPreferenceRepository;

    public SaveUserPreferenceUseCase(UserPreferenceRepository userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public String execute(UUID userId, String key, String valueJson) {
        return userPreferenceRepository.saveValue(userId, key, valueJson);
    }
}
