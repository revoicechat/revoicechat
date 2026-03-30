package fr.revoicechat.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestUser {

  @Test
  @SuppressWarnings({ "java:S5838", "java:S5863", "EqualsWithItself" })
  void test() {
    var id1 = UUID.randomUUID();
    var user1 = new User();
    user1.setId(id1);
    var user2 = new User();
    user2.setId(id1);
    var user3 = new User();
    user3.setId(UUID.randomUUID());

    assertThat(user1).isEqualTo(user1)
                     .isEqualTo(user2)
                     .hasSameHashCodeAs(user2)
                     .isNotEqualTo(user3)
                     .isNotEqualTo(null)
                     .isNotEqualTo(new Object());
  }
}