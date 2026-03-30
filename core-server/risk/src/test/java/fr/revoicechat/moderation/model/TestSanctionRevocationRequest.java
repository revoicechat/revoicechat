package fr.revoicechat.moderation.model;

import static fr.revoicechat.moderation.model.RequestStatus.*;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestSanctionRevocationRequest {

  public static Stream<Arguments> paramCanRequestAgain() {
    return Stream.of(
        Arguments.of(REJECTED, LocalDateTime.now().minusMonths(6), true),
        Arguments.of(REJECTED, LocalDateTime.now().minusMonths(1), false),
        Arguments.of(REJECTED, LocalDateTime.now(), false),
        Arguments.of(ACCEPTED, LocalDateTime.now().minusMonths(6), false),
        Arguments.of(ACCEPTED, LocalDateTime.now().minusMonths(1), false),
        Arguments.of(ACCEPTED, LocalDateTime.now(), false),
        Arguments.of(CREATED, LocalDateTime.now().minusMonths(6), false),
        Arguments.of(CREATED, LocalDateTime.now().minusMonths(1), false),
        Arguments.of(CREATED, LocalDateTime.now(), false),
        Arguments.of(null, LocalDateTime.now().minusMonths(6), false),
        Arguments.of(null, LocalDateTime.now().minusMonths(1), false),
        Arguments.of(null, LocalDateTime.now(), false)
    );
  }

  @ParameterizedTest
  @MethodSource("paramCanRequestAgain")
  void testCanRequestAgain(RequestStatus status, LocalDateTime requestAt, boolean result) {
    var request = new SanctionRevocationRequest();
    request.setStatus(status);
    request.setRequestAt(requestAt);
    Assertions.assertThat(request.canRequestAgain()).isEqualTo(result);
  }
}