package fr.revoicechat.risk.service;

import java.util.List;
import java.util.Set;

import fr.revoicechat.i18n.TranslationUtils;
import fr.revoicechat.risk.representation.RiskCategoryRepresentation;
import fr.revoicechat.risk.type.RiskCategory;
import fr.revoicechat.risk.type.RiskType;
import fr.revoicechat.risk.type.RoomEntityRiskType;
import fr.revoicechat.risk.type.ServerEntityRiskType;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RiskCategoryService {

  public List<RiskCategoryRepresentation> findAll() {
    return getRisks(RiskType.class);
  }

  public List<RiskCategoryRepresentation> forServer() {
    return getRisks(ServerEntityRiskType.class);
  }

  public List<RiskCategoryRepresentation> forRoom() {
    return getRisks(RoomEntityRiskType.class);
  }

  private List<RiskCategoryRepresentation> getRisks(Class<?> forType) {
    return RiskTypeClassesHolder.INSTANCE.getRiskType()
                                         .stream()
                                         .filter(Class::isEnum)
                                         .filter(clazz -> clazz.isAnnotationPresent(RiskCategory.class))
                                         .map(clazz -> new RiskData(clazz.getEnumConstants(), clazz.getAnnotation(RiskCategory.class)))
                                         .filter(data -> data.riskTypes().length != 0)
                                         .filter(data -> forType.isInstance(data.riskTypes()[0]))
                                         .map(this::mapToRiskCategory)
                                         .toList();
  }

  public RiskCategoryRepresentation mapToRiskCategory(RiskData data) {
    var risk = data.riskTypes[0];
    return new RiskCategoryRepresentation(
        data.category.value(),
        TranslationUtils.translate(risk.fileName(), data.category.value()),
        Set.of(data.riskTypes)
    );
  }

  @SuppressWarnings("java:S6218") // equals and hashcode is not necessary here
  public record RiskData(RiskType[] riskTypes, RiskCategory category) {}
}
