package fr.revoicechat.core.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_EMOTE")
public class Emote {

  @Id
  private UUID id;

  @OneToOne(optional = false, cascade = CascadeType.ALL)
  @JoinColumn(name = "MEDIA_ID", referencedColumnName = "ID")
  private MediaData media;

  /** It can be a user id or a server id */
  private UUID entity;
  private String content;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "RVC_EMOTE_KEYWORDS",
      joinColumns = @JoinColumn(name = "EMOTE_ID")
  )
  @Column(name = "KEYWORD")
  private List<String> keywords;

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public MediaData getMedia() {
    return media;
  }

  public void setMedia(final MediaData media) {
    this.media = media;
  }

  public UUID getEntity() {
    return entity;
  }

  public void setEntity(final UUID entity) {
    this.entity = entity;
  }

  public String getContent() {
    return content;
  }

  public void setContent(final String content) {
    this.content = content;
  }

  public List<String> getKeywords() {
    return keywords;
  }

  public void setKeywords(final List<String> keywords) {
    this.keywords = keywords;
  }
}
