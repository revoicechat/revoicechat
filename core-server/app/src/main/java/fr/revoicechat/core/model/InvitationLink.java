package fr.revoicechat.core.model;

import static fr.revoicechat.core.model.InvitationLinkStatus.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_INVATION_LINK")
public class InvitationLink {
  @Id
  private UUID id;
  @Enumerated(EnumType.STRING)
  private InvitationType type;
  @ManyToOne
  @JoinColumn(name="SENDER_ID")
  private User sender;
  @ManyToOne
  @JoinColumn(name="APPLIER_ID")
  private User applier;
  @ManyToOne
  @JoinColumn(name="TARGETED_SERVER_ID")
  private Server targetedServer;
  @Enumerated(EnumType.STRING)
  private InvitationLinkStatus status;

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public InvitationType getType() {
    return type;
  }

  public void setType(final InvitationType type) {
    this.type = type;
  }

  public User getSender() {
    return sender;
  }

  public void setSender(final User sender) {
    this.sender = sender;
  }

  public User getApplier() {
    return applier;
  }

  public void setApplier(final User applier) {
    this.applier = applier;
  }

  public Server getTargetedServer() {
    return targetedServer;
  }

  public void setTargetedServer(final Server targetedServer) {
    this.targetedServer = targetedServer;
  }

  public InvitationLinkStatus getStatus() {
    return status;
  }

  public void setStatus(final InvitationLinkStatus status) {
    this.status = status;
  }

  public boolean isValid() {
    return List.of(CREATED, PERMANENT).contains(getStatus());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final InvitationLink that = (InvitationLink) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
