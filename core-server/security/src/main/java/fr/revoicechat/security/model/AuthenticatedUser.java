package fr.revoicechat.security.model;

import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_USER")
public class AuthenticatedUser {
  @Id
  private UUID id;
  private String login;
  private String displayName;
  private String password;
  @Column(name = "BASE_32_SECRET")
  private String base32Secret;

  @Enumerated(EnumType.STRING)
  private UserType type = UserType.USER;

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(final String login) {
    this.login = login;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(final String displayName) {
    this.displayName = displayName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public String getBase32Secret() {
    return base32Secret;
  }

  public void setBase32Secret(final String base32Secret) {
    this.base32Secret = base32Secret;
  }

  public UserType getType() {
    return type;
  }

  public void setType(final UserType type) {
    this.type = type;
  }

  public Set<String> getRoles() {
    return getType().getRoles();
  }
}
