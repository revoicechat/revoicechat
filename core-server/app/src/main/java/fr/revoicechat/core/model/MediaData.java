package fr.revoicechat.core.model;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_MEDIA_DATA")
public class MediaData {
  @Id
  private UUID id;
  @Enumerated(EnumType.STRING)
  private FileType type;
  @Enumerated(EnumType.STRING)
  private MediaOrigin origin;
  private String name;
  @Enumerated(EnumType.STRING)
  private MediaDataStatus status;

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public FileType getType() {
    return type;
  }

  public void setType(final FileType type) {
    this.type = type;
  }

  public MediaOrigin getOrigin() {
    return origin;
  }

  public void setOrigin(final MediaOrigin origin) {
    this.origin = origin;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public MediaDataStatus getStatus() {
    return status;
  }

  public void setStatus(final MediaDataStatus status) {
    this.status = status;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) { return true; }
    if (!(o instanceof MediaData media)) { return false; }
    return Objects.equals(getId(), media.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
