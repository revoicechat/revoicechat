package fr.revoicechat.risk.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestUserRoleMembership {

  @Test
  @SuppressWarnings({ "java:S5838", "java:S5863", "EqualsWithItself" })
  void test() {
    var id1 = UUID.randomUUID();
    var user1 = new UserRoleMembership();
    user1.setId(id1);
    var user2 = new UserRoleMembership();
    user2.setId(id1);
    var user3 = new UserRoleMembership();
    user3.setId(UUID.randomUUID());

    assertThat(user1).isEqualTo(user1)
                     .isEqualTo(user2)
                     .hasSameHashCodeAs(user2)
                     .isNotEqualTo(user3)
                     .isNotEqualTo(null)
                     .isNotEqualTo(new Object());
  }
}