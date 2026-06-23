package br.com.autocarehub.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.autocarehub.infrastructure.persistence.entity.UserPreferenceId;
import br.com.autocarehub.infrastructure.persistence.entity.UserPreferenceJpaEntity;

public interface UserPreferenceJpaRepository
        extends JpaRepository<UserPreferenceJpaEntity, UserPreferenceId> {
}
