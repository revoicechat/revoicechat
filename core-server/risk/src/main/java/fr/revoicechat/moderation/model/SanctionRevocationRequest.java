package fr.revoicechat.moderation.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_SANCTION_REVOCATION_REQUEST")
public class SanctionRevocationRequest {
  @Id
  private UUID id;
  @ManyToOne
  @JoinColumn(name = "SANCTION_ID", nullable = false)
  private Sanction sanction;
  @Column(nullable = false)
  private String message;
  private RequestStatus status;
  @Column(name = "REQUEST_AT")
  private LocalDateTime requestAt;

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public Sanction getSanction() {
    return sanction;
  }

  public void setSanction(final Sanction sanction) {
    this.sanction = sanction;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  public RequestStatus getStatus() {
    return status;
  }

  public void setStatus(final RequestStatus status) {
    this.status = status;
  }

  public LocalDateTime getRequestAt() {
    return requestAt;
  }

  public void setRequestAt(final LocalDateTime requestAt) {
    this.requestAt = requestAt;
  }

  public boolean canRequestAgain() {
    return getStatus() == RequestStatus.REJECTED
        && LocalDateTime.now().minusMonths(3).isAfter(getRequestAt());
  }
}
