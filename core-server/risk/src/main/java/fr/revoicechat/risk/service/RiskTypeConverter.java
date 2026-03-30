package fr.revoicechat.risk.service;

import fr.revoicechat.risk.type.RiskType;
import jakarta.persistence.AttributeConverter;

public class RiskTypeConverter implements AttributeConverter<RiskType, String> {

  @Override
  public String convertToDatabaseColumn(RiskType riskType) {
    if (riskType == null) {
      return null;
    }
    return riskType.name();
  }

  @Override
  public RiskType convertToEntityAttribute(final String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return new DefaultRiskType(value);
  }
}
