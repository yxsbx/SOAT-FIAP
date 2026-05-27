package br.com.autocarehub.infrastructure.persistence.entity;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserPreferenceId implements Serializable {

    private UUID userId;
    private String prefKey;
}
