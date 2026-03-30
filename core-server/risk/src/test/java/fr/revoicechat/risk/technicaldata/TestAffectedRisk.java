package fr.revoicechat.risk.technicaldata;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestAffectedRisk {

  private static final AffectedRisk RISK_1 = new AffectedRisk(null, null, UUID.fromString("cafcb646-dc66-4376-bc81-2a9e2ea9fbe6"), 1);
  private static final AffectedRisk RISK_2 = new AffectedRisk(null, null, UUID.fromString("b22e3986-3015-4fd8-bb00-183131850ab2"), 1);
  private static final AffectedRisk RISK_3 = new AffectedRisk(null, null, null, 1);
  private static final AffectedRisk RISK_4 = new AffectedRisk(null, null, UUID.fromString("cafcb646-dc66-4376-bc81-2a9e2ea9fbe6"), 3);
  private static final AffectedRisk RISK_5 = new AffectedRisk(null, null, UUID.fromString("b22e3986-3015-4fd8-bb00-183131850ab2"), 3);
  private static final AffectedRisk RISK_6 = new AffectedRisk(null, null, null, 3);
  private static final AffectedRisk RISK_7 = new AffectedRisk(null, null, UUID.fromString("cafcb646-dc66-4376-bc81-2a9e2ea9fbe6"), 2);
  private static final AffectedRisk RISK_8 = new AffectedRisk(null, null, UUID.fromString("b22e3986-3015-4fd8-bb00-183131850ab2"), 2);
  private static final AffectedRisk RISK_9 = new AffectedRisk(null, null, null, 2);

  public static Stream<Arguments> compareToValues() {
    return Stream.of(
        Arguments.of(RISK_1, RISK_1, 0),
        Arguments.of(RISK_1, RISK_2, 1),
        Arguments.of(RISK_1, RISK_3, -1),
        Arguments.of(RISK_1, RISK_4, -1),
        Arguments.of(RISK_1, RISK_5, -1),
        Arguments.of(RISK_1, RISK_6, -1),
        Arguments.of(RISK_1, RISK_7, -1),
        Arguments.of(RISK_1, RISK_8, -1),
        Arguments.of(RISK_1, RISK_9, -1),
        Arguments.of(RISK_2, RISK_1, -1),
        Arguments.of(RISK_2, RISK_2, 0),
        Arguments.of(RISK_2, RISK_3, -1),
        Arguments.of(RISK_2, RISK_4, -1),
        Arguments.of(RISK_2, RISK_5, -1),
        Arguments.of(RISK_2, RISK_6, -1),
        Arguments.of(RISK_2, RISK_7, -1),
        Arguments.of(RISK_2, RISK_8, -1),
        Arguments.of(RISK_2, RISK_9, -1),
        Arguments.of(RISK_3, RISK_1, 1),
        Arguments.of(RISK_3, RISK_2, 1),
        Arguments.of(RISK_3, RISK_3, 0),
        Arguments.of(RISK_3, RISK_4, -1),
        Arguments.of(RISK_3, RISK_5, -1),
        Arguments.of(RISK_3, RISK_6, -1),
        Arguments.of(RISK_3, RISK_7, -1),
        Arguments.of(RISK_3, RISK_8, -1),
        Arguments.of(RISK_3, RISK_9, -1),
        Arguments.of(RISK_4, RISK_1, 1),
        Arguments.of(RISK_4, RISK_2, 1),
        Arguments.of(RISK_4, RISK_3, 1),
        Arguments.of(RISK_4, RISK_4, 0),
        Arguments.of(RISK_4, RISK_5, 1),
        Arguments.of(RISK_4, RISK_6, -1),
        Arguments.of(RISK_4, RISK_7, 1),
        Arguments.of(RISK_4, RISK_8, 1),
        Arguments.of(RISK_4, RISK_9, 1),
        Arguments.of(RISK_5, RISK_1, 1),
        Arguments.of(RISK_5, RISK_2, 1),
        Arguments.of(RISK_5, RISK_3, 1),
        Arguments.of(RISK_5, RISK_4, -1),
        Arguments.of(RISK_5, RISK_5, 0),
        Arguments.of(RISK_5, RISK_6, -1),
        Arguments.of(RISK_5, RISK_7, 1),
        Arguments.of(RISK_5, RISK_8, 1),
        Arguments.of(RISK_5, RISK_9, 1),
        Arguments.of(RISK_6, RISK_1, 1),
        Arguments.of(RISK_6, RISK_2, 1),
        Arguments.of(RISK_6, RISK_3, 1),
        Arguments.of(RISK_6, RISK_4, 1),
        Arguments.of(RISK_6, RISK_5, 1),
        Arguments.of(RISK_6, RISK_6, 0),
        Arguments.of(RISK_6, RISK_7, 1),
        Arguments.of(RISK_6, RISK_8, 1),
        Arguments.of(RISK_6, RISK_9, 1),
        Arguments.of(RISK_7, RISK_1, 1),
        Arguments.of(RISK_7, RISK_2, 1),
        Arguments.of(RISK_7, RISK_3, 1),
        Arguments.of(RISK_7, RISK_4, -1),
        Arguments.of(RISK_7, RISK_5, -1),
        Arguments.of(RISK_7, RISK_6, -1),
        Arguments.of(RISK_7, RISK_7, 0),
        Arguments.of(RISK_7, RISK_8, 1),
        Arguments.of(RISK_7, RISK_9, -1),
        Arguments.of(RISK_8, RISK_1, 1),
        Arguments.of(RISK_8, RISK_2, 1),
        Arguments.of(RISK_8, RISK_3, 1),
        Arguments.of(RISK_8, RISK_4, -1),
        Arguments.of(RISK_8, RISK_5, -1),
        Arguments.of(RISK_8, RISK_6, -1),
        Arguments.of(RISK_8, RISK_7, -1),
        Arguments.of(RISK_8, RISK_8, 0),
        Arguments.of(RISK_8, RISK_9, -1),
        Arguments.of(RISK_9, RISK_1, 1),
        Arguments.of(RISK_9, RISK_2, 1),
        Arguments.of(RISK_9, RISK_3, 1),
        Arguments.of(RISK_9, RISK_4, -1),
        Arguments.of(RISK_9, RISK_5, -1),
        Arguments.of(RISK_9, RISK_6, -1),
        Arguments.of(RISK_9, RISK_7, 1),
        Arguments.of(RISK_9, RISK_8, 1),
        Arguments.of(RISK_9, RISK_9, 0)
        );
  }

  @ParameterizedTest
  @MethodSource("compareToValues")
  void testCompareTo(AffectedRisk r1, AffectedRisk r2, int expected) {
    Assertions.assertThat(r1.compareTo(r2)).isEqualTo(expected);
  }

  @Test
  void test() {
    var result = Stream.of(
        RISK_1, RISK_2, RISK_3, RISK_4, RISK_5, RISK_6, RISK_7, RISK_8, RISK_9
    ).sorted().toList();
    // Then
    assertThat(result).containsExactly(RISK_2, RISK_1, RISK_3, RISK_8, RISK_7, RISK_9, RISK_5, RISK_4, RISK_6);
  }
}