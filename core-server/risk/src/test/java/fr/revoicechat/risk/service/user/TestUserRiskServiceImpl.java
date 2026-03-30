package fr.revoicechat.risk.service.user;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.revoicechat.risk.service.AffectedRiskService;
import fr.revoicechat.risk.technicaldata.AffectedRisk;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import fr.revoicechat.risk.type.RiskType;
import io.quarkus.security.UnauthorizedException;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestUserRiskServiceImpl {

  @Test
  void testRiskWithSamePriority() {
    int priority = 5;
    var affectedRiskService = new AffectedRiskServiceMock(new AffectedRisk(null, null, null, 5));
    var service = new UserRiskServiceImpl(affectedRiskService);
    assertThatCode(() -> service.controlRiskPriority(priority, null, null))
        .doesNotThrowAnyException();
  }

  @Test
  void testRiskWithHigerPriority() {
    int priority = 5;
    var affectedRiskService = new AffectedRiskServiceMock(new AffectedRisk(null, null, null, 10));
    var service = new UserRiskServiceImpl(affectedRiskService);
    assertThatCode(() -> service.controlRiskPriority(priority, null, null))
        .doesNotThrowAnyException();
  }

  @Test
  void testRiskWithLowerPriority() {
    int priority = 5;
    var affectedRiskService = new AffectedRiskServiceMock(new AffectedRisk(null, null, null, 1));
    var service = new UserRiskServiceImpl(affectedRiskService);
    assertThatThrownBy(() -> service.controlRiskPriority(priority, null, null))
              .isInstanceOf(UnauthorizedException.class);
  }

  @Test
  void testRiskWithNoPriority() {
    int priority = 5;
    var affectedRiskService = new AffectedRiskServiceMock(null);
    var service = new UserRiskServiceImpl(affectedRiskService);
    assertThatThrownBy(() -> service.controlRiskPriority(priority, null, null))
        .isInstanceOf(UnauthorizedException.class);
  }

  private record AffectedRiskServiceMock(AffectedRisk risk) implements AffectedRiskService {
    @Override
    public Optional<AffectedRisk> get(final RiskEntity entity, final RiskType riskType) {
      return Optional.ofNullable(risk);
    }

    @Override
    public Optional<AffectedRisk> get(final UUID userId, final RiskEntity entity, final RiskType riskType) {
      return Optional.ofNullable(risk);
    }
  }
}