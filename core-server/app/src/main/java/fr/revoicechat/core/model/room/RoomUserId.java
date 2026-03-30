package fr.revoicechat.core.model.room;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/** Composite ID of {@link RoomReadStatus}. */
public class RoomUserId implements Serializable {
  private UUID user;
  private UUID room;

  public UUID getUser() {
    return user;
  }

  public void setUser(final UUID user) {
    this.user = user;
  }

  public UUID getRoom() {
    return room;
  }

  public void setRoom(final UUID room) {
    this.room = room;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final RoomUserId that = (RoomUserId) o;
    return Objects.equals(getUser(), that.getUser()) && Objects.equals(getRoom(), that.getRoom());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUser(), getRoom());
  }
}