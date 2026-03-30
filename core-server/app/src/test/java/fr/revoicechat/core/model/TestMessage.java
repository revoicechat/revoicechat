package fr.revoicechat.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestMessage {

  @Test
  @SuppressWarnings({ "java:S5838", "java:S5863", "EqualsWithItself" })
  void test() {
    var id1 = UUID.randomUUID();
    var message1 = new Message();
    message1.setId(id1);
    var message2 = new Message();
    message2.setId(id1);
    var message3 = new Message();
    message3.setId(UUID.randomUUID());

    assertThat(message1).isEqualTo(message1)
                        .isEqualTo(message2)
                        .hasSameHashCodeAs(message2)
                        .isNotEqualTo(message3)
                        .isNotEqualTo(null)
                        .isNotEqualTo(new Object());
  }
}