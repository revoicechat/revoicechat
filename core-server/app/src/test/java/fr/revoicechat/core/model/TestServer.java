package fr.revoicechat.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestServer {

  @Test
  @SuppressWarnings({ "java:S5838", "java:S5863", "EqualsWithItself" })
  void test() {
    var id1 = UUID.randomUUID();
    var server1 = new Server();
    server1.setId(id1);
    var server2 = new Server();
    server2.setId(id1);
    var server3 = new Server();
    server3.setId(UUID.randomUUID());

    assertThat(server1).isEqualTo(server1)
                       .isEqualTo(server2)
                       .hasSameHashCodeAs(server2)
                       .isNotEqualTo(server3)
                       .isNotEqualTo(null)
                       .isNotEqualTo(new Object());
  }
}