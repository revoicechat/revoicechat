package fr.revoicechat.core.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import fr.revoicechat.core.model.ServerUser.ServerUserId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_SERVER_USER")
@IdClass(ServerUserId.class)
public class ServerUser {

  @Id
  @ManyToOne
  @JoinColumn(name = "USER_ID", nullable = false)
  private User user;
  @Id
  @ManyToOne
  @JoinColumn(name = "SERVER_ID", nullable = false)
  private Server server;

  public User getUser() {
    return user;
  }

  public ServerUser setUser(final User user) {
    this.user = user;
    return this;
  }

  public Server getServer() {
    return server;
  }

  public ServerUser setServer(final Server server) {
    this.server = server;
    return this;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final ServerUser that = (ServerUser) o;
    return Objects.equals(getUser(), that.getUser()) && Objects.equals(getServer(), that.getServer());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUser(), getServer());
  }

  public static class ServerUserId implements Serializable {
    private UUID user;
    private UUID server;

    public UUID getUser() {
      return user;
    }

    public void setUser(final UUID user) {
      this.user = user;
    }

    public UUID getServer() {
      return server;
    }

    public void setServer(final UUID server) {
      this.server = server;
    }

    @Override
    public boolean equals(final Object o) {
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      final ServerUserId that = (ServerUserId) o;
      return Objects.equals(getUser(), that.getUser()) && Objects.equals(getServer(), that.getServer());
    }

    @Override
    public int hashCode() {
      return Objects.hash(getUser(), getServer());
    }
  }
}
