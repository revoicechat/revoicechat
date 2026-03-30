package fr.revoicechat.risk.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestRisk {

  @Test
  @SuppressWarnings({ "java:S5838", "java:S5863", "EqualsWithItself" })
  void test() {
    var id1 = UUID.randomUUID();
    var risk1 = new Risk();
    risk1.setId(id1);
    var risk2 = new Risk();
    risk2.setId(id1);
    var risk3 = new Risk();
    risk3.setId(UUID.randomUUID());

    assertThat(risk1).isEqualTo(risk1)
                     .isEqualTo(risk2)
                     .hasSameHashCodeAs(risk2)
                     .isNotEqualTo(risk3)
                     .isNotEqualTo(null)
                     .isNotEqualTo(new Object());
  }
}