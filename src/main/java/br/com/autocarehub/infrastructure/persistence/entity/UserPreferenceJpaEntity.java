package br.com.autocarehub.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_preferences")
@IdClass(UserPreferenceId.class)
public class UserPreferenceJpaEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Id
    @Column(name = "pref_key", nullable = false, length = 80)
    private String prefKey;

    @Column(name = "value_json", nullable = false)
    private String valueJson;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
