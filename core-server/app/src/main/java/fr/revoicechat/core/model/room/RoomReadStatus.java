package fr.revoicechat.core.model.room;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_ROOM_READ_STATUS")
@IdClass(RoomUserId.class)
public class RoomReadStatus {

  @Id
  @ManyToOne
  @JoinColumn(name = "USER_ID", nullable = false)
  private User user;
  @Id
  @ManyToOne
  @JoinColumn(name = "ROOM_ID", nullable = false)
  private Room room;
  /** We do not point at {@link Message} because it could be deleted */
  @Column(name = "LAST_READ_MESSAGE_ID")
  private UUID lastMessageId;
  @Column(name = "LAST_READ_AT")
  private OffsetDateTime lastReadAt;

  public User getUser() {
    return user;
  }

  public RoomReadStatus setUser(final User user) {
    this.user = user;
    return this;
  }

  public Room getRoom() {
    return room;
  }

  public RoomReadStatus setRoom(final Room room) {
    this.room = room;
    return this;
  }

  public UUID getLastMessageId() {
    return lastMessageId;
  }

  public void setLastMessageId(final UUID lastMessageId) {
    this.lastMessageId = lastMessageId;
  }

  public OffsetDateTime getLastReadAt() {
    return lastReadAt;
  }

  public void setLastReadAt(final OffsetDateTime lastReadAt) {
    this.lastReadAt = lastReadAt;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final RoomReadStatus that = (RoomReadStatus) o;
    return Objects.equals(getUser(), that.getUser()) && Objects.equals(getRoom(), that.getRoom());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUser(), getRoom());
  }
}
