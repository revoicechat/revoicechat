package fr.revoicechat.core.technicaldata.message;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestNewMessage {

  @Test
  void testOnEmptyMessage() {
    var creation = new NewMessage("", null, List.of());
    Assertions.assertThat(creation.text()).isEmpty();
  }

  @Test
  void testOnMessageNull() {
    var creation = new NewMessage(null, null, List.of());
    Assertions.assertThat(creation.text()).isEmpty();
  }

  @Test
  void test() {
    var creation = new NewMessage("  this is a test  ", null, List.of());
    Assertions.assertThat(creation.text()).isEqualTo("this is a test");
  }
}