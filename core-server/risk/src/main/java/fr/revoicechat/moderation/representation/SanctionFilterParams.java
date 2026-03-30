package fr.revoicechat.moderation.representation;

import java.util.UUID;
import jakarta.ws.rs.QueryParam;

import fr.revoicechat.moderation.model.SanctionType;

public class SanctionFilterParams {
  @QueryParam("userId")
  private UUID userId;

  @QueryParam("active")
  private Boolean active;

  @QueryParam("type")
  private SanctionType type;

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(final UUID userId) {
    this.userId = userId;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(final Boolean active) {
    this.active = active;
  }

  public SanctionType getType() {
    return type;
  }

  public void setType(final SanctionType type) {
    this.type = type;
  }
}
