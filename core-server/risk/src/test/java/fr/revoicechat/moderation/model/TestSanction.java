package fr.revoicechat.moderation.model;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TestSanction {

  public static final UUID UUID = java.util.UUID.randomUUID();
  public static final LocalDateTime TOMORROW = LocalDateTime.now().plusDays(1);
  public static final LocalDateTime YESTERDAY = LocalDateTime.now().minusDays(1);

  public static Stream<Arguments> valueSanctionIsActive() {
    return Stream.of(
        Arguments.of(null, null, null, true),
        Arguments.of(null, null, TOMORROW, true),
        Arguments.of(null, null, YESTERDAY, false),
        Arguments.of(UUID, null, null, true),
        Arguments.of(UUID, TOMORROW, null, true),
        Arguments.of(UUID, YESTERDAY, null, false),
        Arguments.of(null, null, null, true),
        Arguments.of(null, TOMORROW, null, true),
        Arguments.of(null, YESTERDAY, null, true)
    );
  }

  @ParameterizedTest
  @MethodSource("valueSanctionIsActive")
  void testSanctionIsActive(UUID revokedBy, LocalDateTime revokedAt, LocalDateTime expiresAt, boolean isActive) {
    Sanction sanction = new Sanction();
    sanction.setRevokedBy(revokedBy);
    sanction.setRevokedAt(revokedAt);
    sanction.setExpiresAt(expiresAt);
    Assertions.assertThat(sanction.isActive()).isEqualTo(isActive);
  }
}