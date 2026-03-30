package fr.revoicechat.risk.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestRiskTypeConverter {

  @Test
  void testConvertToDatabaseColumnCaseNull() {
    assertThat(new RiskTypeConverter().convertToDatabaseColumn(null)).isNull();
  }

  @Test
  void testConvertToDatabaseColumn() {
    assertThat(new RiskTypeConverter().convertToDatabaseColumn(new DefaultRiskType("test"))).isEqualTo("test");
  }

  @Test
  void testConvertToEntityAttributeCaseNull() {
    assertThat(new RiskTypeConverter().convertToEntityAttribute(null)).isNull();
  }

  @Test
  void testConvertToEntityAttributeCaseBlank() {
    assertThat(new RiskTypeConverter().convertToEntityAttribute("  ")).isNull();
  }

  @Test
  void testConvertToEntityAttribute
      () {
    assertThat(new RiskTypeConverter().convertToEntityAttribute("test")).isEqualTo(new DefaultRiskType("test"));
  }
}