package fr.revoicechat.risk.representation;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.revoicechat.risk.service.risk.TranslatedRiskTypeSerializer;
import fr.revoicechat.risk.type.RiskType;

public record RiskCategoryRepresentation(
    String type,
    String title,
    List<TranslatedRisk> risks
) {

  public RiskCategoryRepresentation(final String type, final String title, Set<RiskType> risks) {
    this(type, title, risks.stream().sorted().map(TranslatedRisk::new).toList());
  }

  @JsonSerialize(using = TranslatedRiskTypeSerializer.class)
  public record TranslatedRisk(RiskType type) {}
}
