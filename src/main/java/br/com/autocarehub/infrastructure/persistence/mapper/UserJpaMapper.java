package br.com.autocarehub.infrastructure.persistence.mapper;

import br.com.autocarehub.domain.User;
import br.com.autocarehub.domain.UserRole;
import br.com.autocarehub.infrastructure.persistence.entity.UserJpaEntity;

public final class UserJpaMapper {

    private UserJpaMapper() {
    }

    public static User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPasswordHash(),
                UserRole.valueOf(entity.getRole()),
                entity.getCustomerId(),
                entity.isActive(),
                entity.getCreatedAt()
        );
    }
}
