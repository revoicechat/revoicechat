package fr.revoicechat.core.model;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import fr.revoicechat.notification.model.ActiveStatus;
import fr.revoicechat.notification.model.NotificationRegistrable;
import fr.revoicechat.security.model.AuthenticatedUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_USER")
public class User implements NotificationRegistrable, AuthenticatedUser {
  @Id
  private UUID id;

  @Column(unique = true)
  private String email;
  @Column(unique = true, nullable = false)
  private String login;
  @Column(nullable = false)
  private String displayName;
  private String password;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ActiveStatus status = ActiveStatus.ONLINE;
  private OffsetDateTime createdDate;
  @Enumerated(EnumType.STRING)
  private UserType type = UserType.USER;
  /**
   * The settings do not currently drive the backend behaviour, but only the frontend behaviour.
   * It's stored as a string so the system does not parse the value into an object.
    */
  @Column(columnDefinition = "TEXT")
  private String settings;

  public User() {
    super();
  }

  @Override
  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  @Override
  public String getLogin() {
    return login;
  }

  public void setLogin(final String login) {
    this.login = login;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(final String username) {
    this.displayName = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public OffsetDateTime getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(final OffsetDateTime createdDate) {
    this.createdDate = createdDate;
  }

  @Override
  public ActiveStatus getStatus() {
    return status;
  }

  public void setStatus(final ActiveStatus status) {
    this.status = status;
  }

  public UserType getType() {
    return type;
  }

  public void setType(final UserType type) {
    this.type = type;
  }

  public String getSettings() {
    return this.settings;
  }

  public void setSettings(final String settings) {
    this.settings = settings;
  }

  @Override
  public Set<String> getRoles() {
    return getType().getRoles();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User user)) {
      return false;
    }
    return Objects.equals(getId(), user.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
