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
  @Column(name = "TOTP_SECRET")
  private String totpSecret;
  @Column(name = "TOTP_STATUS", nullable = false)
  @Enumerated(EnumType.STRING)
  private TotpStatus totpStatus = TotpStatus.INACTIVE;
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

  public String getTotpSecret() {
    return totpSecret;
  }

  public void setTotpSecret(final String totpSecret) {
    this.totpSecret = totpSecret;
  }

  public TotpStatus getTotpStatus() {
    return totpStatus;
  }

  public void setTotpStatus(final TotpStatus totpStatus) {
    this.totpStatus = totpStatus;
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
