package fr.revoicechat.risk.service;

import java.util.Set;

import org.reflections.Reflections;

import fr.revoicechat.risk.type.RiskType;

enum RiskTypeClassesHolder {
  INSTANCE(getRiskTypeClasses());

  private final Set<Class<? extends RiskType>> riskType;

  RiskTypeClassesHolder(final Set<Class<? extends RiskType>> riskType) {
    this.riskType = riskType;
  }

  private static Set<Class<? extends RiskType>> getRiskTypeClasses() {
    Reflections reflections = new Reflections("fr.revoicechat");
    return reflections.getSubTypesOf(RiskType.class);
  }

  public Set<Class<? extends RiskType>> getRiskType() {
    return riskType;
  }
}