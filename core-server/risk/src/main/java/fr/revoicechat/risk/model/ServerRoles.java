package fr.revoicechat.risk.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_SERVER_ROLES")
public class ServerRoles {
  @Id
  private UUID id;

  @Column(nullable = false)
  private String name;
  private String color;
  private int priority;
  /** When a user join a server, he automatically has this role */
  private boolean defaultRole;
  @Column(name = "SERVER_ID", nullable = false, updatable = false)
  private UUID server;

  @ManyToMany
  @JoinTable(name = "RVC_USER_SERVER_ROLES",
      joinColumns = @JoinColumn(name = "SERVER_ROLE_ID", referencedColumnName = "ID"),
      inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"))
  private List<UserRoleMembership> users;

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

  public String getColor() {
    return color;
  }

  public void setColor(final String color) {
    this.color = color;
  }

  public boolean isDefaultRole() {
    return defaultRole;
  }

  public void setDefaultRole(final boolean defaultRole) {
    this.defaultRole = defaultRole;
  }

  public UUID getServer() {
    return server;
  }

  public void setServer(final UUID server) {
    this.server = server;
  }

  public List<UserRoleMembership> getUsers() {
    return users;
  }

  public void setUsers(final List<UserRoleMembership> users) {
    this.users = users;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(final int priority) {
    this.priority = priority;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ServerRoles role)) {
      return false;
    }
    return Objects.equals(getId(), role.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}

