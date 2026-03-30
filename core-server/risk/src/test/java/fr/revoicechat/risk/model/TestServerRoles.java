package fr.revoicechat.risk.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestServerRoles {

  @Test
  @SuppressWarnings({ "java:S5838", "java:S5863", "EqualsWithItself" })
  void test() {
    var id1 = UUID.randomUUID();
    var serverRoles1 = new ServerRoles();
    serverRoles1.setId(id1);
    var serverRoles2 = new ServerRoles();
    serverRoles2.setId(id1);
    var serverRoles3 = new ServerRoles();
    serverRoles3.setId(UUID.randomUUID());

    assertThat(serverRoles1).isEqualTo(serverRoles1)
                     .isEqualTo(serverRoles2)
                     .hasSameHashCodeAs(serverRoles2)
                     .isNotEqualTo(serverRoles3)
                     .isNotEqualTo(null)
                     .isNotEqualTo(new Object());
  }
}