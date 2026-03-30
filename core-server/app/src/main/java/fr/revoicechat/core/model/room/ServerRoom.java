package fr.revoicechat.core.model.room;

import java.util.Objects;

import fr.revoicechat.core.model.Server;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_SERVER_ROOM")
@PrimaryKeyJoinColumn(name = "ID")
public class ServerRoom extends Room {
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private RoomType type;
  @ManyToOne
  @JoinColumn(name="SERVER_ID", nullable=false)
  private Server server;

  public ServerRoom() {
    super();
  }

  public RoomType getType() {
    return type;
  }

  public void setType(final RoomType type) {
    this.type = type;
  }

  public Server getServer() {
    return server;
  }

  public void setServer(final Server server) {
    this.server = server;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) { return true; }
    if (!(o instanceof ServerRoom room)) { return false; }
    return Objects.equals(getId(), room.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
