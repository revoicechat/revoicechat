package fr.revoicechat.risk.service;

import java.util.UUID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.revoicechat.moderation.model.SanctionType;
import fr.revoicechat.risk.RisksMembershipData;
import fr.revoicechat.risk.retriever.ServerIdRetriever;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import fr.revoicechat.risk.type.RiskType;
import io.quarkus.security.UnauthorizedException;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestRisksMembershipInterceptor {

  @BeforeEach
  void setUp() {
    RisksMembershipInterceptor.cleanServices();
  }

  @Test
  void test() {
    var service = CDI.current().select(RisksMembershipInterceptorTestService.class).get();
    RisksMembershipInterceptor.setRiskService(new RiskServiceMock(true));
    var server = UUID.randomUUID();
    Assertions.assertThatCode(() -> service.test(server)).doesNotThrowAnyException();
  }

  @Test
  void testError() {
    var service = CDI.current().select(RisksMembershipInterceptorTestService.class).get();
    RisksMembershipInterceptor.setRiskService(new RiskServiceMock(false));
    var server = UUID.randomUUID();
    Assertions.assertThatThrownBy(() -> service.test(server)).isInstanceOf(UnauthorizedException.class);
  }

  @ApplicationScoped
  static class RisksMembershipInterceptorTestService {

    @SuppressWarnings("unused")
    @RisksMembershipData(risks = "TEST", retriever = ServerIdRetriever.class)
    void test(UUID server) {
      // nothing here
    }
  }

  private record RiskServiceMock(boolean hasRisk) implements RiskService {

    @Override
    public boolean hasRisk(final UUID userId, final RiskEntity entity, final RiskType riskType) {
      return hasRisk;
    }

    @Override
    public boolean hasRisk(final UUID userId, final RiskEntity entity, final RiskType riskType, final SanctionType sanctionType) {
      return hasRisk;
    }

    @Override
    public boolean hasRisk(final RiskEntity entity, final RiskType riskType, final SanctionType sanctionType) {
      return hasRisk;
    }

    @Override
    public boolean hasRisk(final RiskEntity entity, final RiskType riskType) {
      return hasRisk;
    }
  }
}