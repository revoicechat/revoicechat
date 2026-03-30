package fr.revoicechat.core.model.room;

import java.util.List;
import java.util.Objects;

import fr.revoicechat.core.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_PRIVATE_MESSAGE_ROOM")
@PrimaryKeyJoinColumn(name = "ID")
public class PrivateMessageRoom extends Room {
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PrivateMessageMode mode;

  @ManyToMany
  @JoinTable(name = "RVC_PRIVATE_MESSAGE_ROOM_USERS",
      joinColumns = @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID"),
      inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"))
  private List<User> users;

  public PrivateMessageRoom() {
    super();
  }

  @Override
  public RoomType getType() {
    return RoomType.PRIVATE_MESSAGE;
  }

  public PrivateMessageMode getMode() {
    return mode;
  }

  public void setMode(final PrivateMessageMode mode) {
    this.mode = mode;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(final List<User> users) {
    this.users = users;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) { return true; }
    if (!(o instanceof PrivateMessageRoom room)) { return false; }
    return Objects.equals(getId(), room.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
