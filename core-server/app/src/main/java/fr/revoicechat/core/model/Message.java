package fr.revoicechat.core.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import fr.revoicechat.core.model.room.Room;
import fr.revoicechat.core.repository.jpa.MessageReactionsConverter;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_MESSAGE")
public class Message {
  @Id
  private UUID id;
  @Column(columnDefinition = "TEXT")
  private String text;
  private OffsetDateTime createdDate;
  @Nullable
  private OffsetDateTime updatedDate;
  @ManyToOne
  @JoinColumn(name="ROOM_ID", nullable=false)
  private Room room;
  @ManyToOne
  @JoinColumn(name="USER_ID", nullable=false)
  private User user;
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "RVC_MEASSAGE_MEDIA",
      joinColumns = @JoinColumn(name = "MEASSAGE_ID", referencedColumnName = "ID"),
      inverseJoinColumns = @JoinColumn(name = "MEDIA_ID", referencedColumnName = "ID"))
  private List<MediaData> mediaDatas;
  @ManyToOne
  @JoinColumn(name="MESSAGE_ID")
  private Message answerTo;
  @Convert(converter = MessageReactionsConverter.class)
  @Column(columnDefinition = "TEXT")
  private MessageReactions reactions;

  public Message() {
    super();
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(final String text) {
    this.text = text;
  }

  public OffsetDateTime getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(final OffsetDateTime createdDate) {
    this.createdDate = createdDate;
  }

  @Nullable
  public OffsetDateTime getUpdatedDate() {
    return updatedDate;
  }

  public void setUpdatedDate(@Nullable OffsetDateTime updatedDate) {
    this.updatedDate = updatedDate;
  }

  public Room getRoom() {
    return room;
  }

  public void setRoom(final Room room) {
    this.room = room;
  }

  public User getUser() {
    return user;
  }

  public void setUser(final User user) {
    this.user = user;
  }

  public List<MediaData> getMediaDatas() {
    if (this.mediaDatas == null) {
      this.mediaDatas = new ArrayList<>();
    }
    return this.mediaDatas;
  }

  public void addMediaData(final MediaData mediaData) {
    if (this.mediaDatas == null) {
      this.mediaDatas = new ArrayList<>();
    }
    mediaDatas.add(mediaData);
  }

  public Message getAnswerTo() {
    return answerTo;
  }

  public void setAnswerTo(final Message answerTo) {
    this.answerTo = answerTo;
  }

  public MessageReactions getReactions() {
    return reactions;
  }

  public void setReactions(final MessageReactions reactions) {
    this.reactions = reactions;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) { return true; }
    if (!(o instanceof Message message)) { return false; }
    return Objects.equals(getId(), message.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }

  @Override
  public String toString() {
    return "Message [%s] %s : %s".formatted(getId(), getCreatedDate(), getText());
  }
}
