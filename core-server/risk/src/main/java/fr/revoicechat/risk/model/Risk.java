package fr.revoicechat.risk.model;

import java.util.Objects;
import java.util.UUID;

import fr.revoicechat.risk.service.RiskTypeConverter;
import fr.revoicechat.risk.type.RiskType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_RISK")
public class Risk {
  @Id
  private UUID id;
  @Column(name = "ENTITY_ID", updatable = false)
  private UUID entity;
  @ManyToOne
  @JoinColumn(name = "ROLE_ID", updatable = false)
  private ServerRoles serverRoles;
  @Convert(converter = RiskTypeConverter.class)
  @Column(name = "RISK_TYPE", columnDefinition = "TEXT")
  private RiskType type;
  @Enumerated(EnumType.STRING)
  private RiskMode mode;

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public UUID getEntity() {
    return entity;
  }

  public void setEntity(final UUID entity) {
    this.entity = entity;
  }

  public ServerRoles getServerRoles() {
    return serverRoles;
  }

  public void setServerRoles(final ServerRoles serverRoles) {
    this.serverRoles = serverRoles;
  }

  public RiskType getType() {
    return type;
  }

  public void setType(final RiskType type) {
    this.type = type;
  }

  public RiskMode getMode() {
    return mode;
  }

  public void setMode(final RiskMode mode) {
    this.mode = mode;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Risk role)) {
      return false;
    }
    return Objects.equals(getId(), role.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
