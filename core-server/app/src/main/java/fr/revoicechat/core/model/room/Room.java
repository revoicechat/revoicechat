package fr.revoicechat.core.model.room;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_ROOM")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Room {

  @Id
  private UUID id;
  @Column
  private String name;

  protected Room() {
    super();
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public abstract RoomType getType();

  public boolean isVoiceRoom() {
    return getType().isVocal();
  }
}
