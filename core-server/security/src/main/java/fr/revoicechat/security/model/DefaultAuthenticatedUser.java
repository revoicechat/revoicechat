package fr.revoicechat.security.model;

import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_USER")
public class DefaultAuthenticatedUser implements AuthenticatedUser {
  @Id
  private UUID id;

  private String login;
  private String displayName;
  private String password;
  @Enumerated(EnumType.STRING)
  private UserType type = UserType.USER;

  @Override
  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
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

  public void setDisplayName(final String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public UserType getType() {
    return type;
  }

  public void setType(final UserType type) {
    this.type = type;
  }

  @Override
  public Set<String> getRoles() {
    return getType().getRoles();
  }
}
