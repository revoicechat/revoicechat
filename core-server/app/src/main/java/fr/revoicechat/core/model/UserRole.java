package fr.revoicechat.core.model;

import java.util.Objects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_SERVER_USER")
public class UserRole {
  @EmbeddedId
  private UserRolePK pk;

  public UserRolePK getPk() {
    return pk;
  }

  public void setPk(final UserRolePK pk) {
    this.pk = pk;
  }

  public void setServer(final Server server) {
    pk.setServer(server);
  }

  public void setUser(final User user) {
    pk.setUser(user);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) { return true; }
    if (!(o instanceof UserRole role)) { return false; }
    return Objects.equals(getPk(), role.getPk());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getPk());
  }

  @Embeddable
  public static final class UserRolePK {
    @OneToOne
    @JoinColumn(name="SERVER_ID", nullable=false, updatable=false)
    private Server server;
    @OneToOne
    @JoinColumn(name="USER_ID", nullable=false, updatable=false)
    private User user;

    public Server getServer() {
      return server;
    }

    public void setServer(final Server server) {
      this.server = server;
    }

    public User getUser() {
      return user;
    }

    public void setUser(final User user) {
      this.user = user;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) { return true; }
      if (!(o instanceof UserRolePK pk)) { return false; }
      return Objects.equals(getServer(), pk.getServer())
             && Objects.equals(getUser(), pk.getUser());
    }

    @Override
    public int hashCode() {
      return Objects.hash(getServer(), getUser());
    }
  }
}
