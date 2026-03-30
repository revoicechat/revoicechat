package fr.revoicechat.notification.model;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestNotificationType {
  private static final Logger LOG = LoggerFactory.getLogger(TestNotificationType.class);

  private static Set<Class<? extends NotificationPayload>> payloads() {
    Reflections reflections = new Reflections("fr.revoicechat");
    return reflections.getSubTypesOf(NotificationPayload.class);
  }

  @ParameterizedTest
  @MethodSource("payloads")
  void testNotificationPayloadIsAnnotated(Class<? extends NotificationPayload> clazz) {
    assertThat(clazz.getAnnotation(NotificationType.class)).isNotNull();
  }

  @Test
  void allNotificationTypesAreUnique() {
    var payloads = payloads();
    Map<String, Class<?>> seen = new HashMap<>();
    for (Class<?> clazz : payloads) {
      NotificationType ann = clazz.getAnnotation(NotificationType.class);
      if (seen.containsKey(ann.name())) {
        fail("""
            Duplicate @NotificationType value "%s" for classes:
              - %s
              - %s""".formatted(ann.name(), clazz.getName(), seen.get(ann.name()).getName()));
      }
      seen.put(ann.name(), clazz);
    }
    seen.forEach((value, clazz) -> LOG.info("type \"{}\" for class \"{}\"", value, clazz.getName()));
  }
}