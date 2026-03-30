package fr.revoicechat.risk.representation;

import java.util.List;
import java.util.Optional;

public record CreatedServerRoleRepresentation(
    String name,
    String color,
    int priority,
    List<RiskRepresentation> risks
) {

  @Override
  public List<RiskRepresentation> risks() {
    return Optional.ofNullable(risks).orElse(List.of());
  }
}
