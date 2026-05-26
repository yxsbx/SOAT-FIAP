package br.com.autocarehub.infrastructure.persistence.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class UserPreferenceId implements Serializable {

  private UUID userId;
  private String prefKey;

  public UserPreferenceId() {}

  public UserPreferenceId(UUID userId, String prefKey) {
    this.userId = userId;
    this.prefKey = prefKey;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public String getPrefKey() {
    return prefKey;
  }

  public void setPrefKey(String prefKey) {
    this.prefKey = prefKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UserPreferenceId that)) {
      return false;
    }
    return Objects.equals(userId, that.userId) && Objects.equals(prefKey, that.prefKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, prefKey);
  }
}
