package fr.revoicechat.stub.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.reflections.Reflections;

import fr.revoicechat.i18n.LocalizedMessage;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestLocalizedMessage {

  private static Set<LocalizedMessage> localizedMessage() {
    return new Reflections("fr.revoicechat").getSubTypesOf(LocalizedMessage.class).stream()
                                            .filter(Class::isEnum)
                                            .map(Class::getEnumConstants)
                                            .flatMap(Stream::of)
                                            .collect(Collectors.toSet());
  }

  @ParameterizedTest
  @MethodSource("localizedMessage")
  void testNotificationPayloadIsAnnotated(LocalizedMessage message) {
    assertThat(message.translate()).isNotEqualTo(message.name());
  }
}