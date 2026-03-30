package fr.revoicechat.risk.web;

import java.util.List;

import fr.revoicechat.risk.representation.RiskCategoryRepresentation;
import fr.revoicechat.risk.service.RiskCategoryService;
import fr.revoicechat.risk.web.api.RiskController;
import jakarta.annotation.security.PermitAll;

public class RiskControllerImpl implements RiskController {

  private final RiskCategoryService riskCategoryService;

  public RiskControllerImpl(final RiskCategoryService riskCategoryService) {
    this.riskCategoryService = riskCategoryService;
  }

  @Override
  @PermitAll
  public List<RiskCategoryRepresentation> getAllRisks() {
    return riskCategoryService.findAll();
  }

  @Override
  @PermitAll
  public List<RiskCategoryRepresentation> getSpecificServerRisks() {
    return riskCategoryService.forServer();
  }

  @Override
  @PermitAll
  public List<RiskCategoryRepresentation> getSpecificRoomRisks() {
    return riskCategoryService.forRoom();
  }
}
