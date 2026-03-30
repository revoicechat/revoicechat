package fr.revoicechat.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.revoicechat.core.model.ServerUser.ServerUserId;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestServerUser {

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

    var user1 = new User();
    user1.setId(id1);
    var user2 = new User();
    user2.setId(id1);
    var user3 = new User();
    user3.setId(UUID.randomUUID());

    var su1 = new ServerUser();
    su1.setServer(server1);
    su1.setUser(user1);
    var su2 = new ServerUser();
    su2.setServer(server2);
    su2.setUser(user2);
    var su3 = new ServerUser();
    su3.setServer(server3);
    su3.setUser(user3);
    var su4 = new ServerUser();
    su4.setServer(server1);
    su4.setUser(user3);
    var su5 = new ServerUser();
    su5.setServer(server3);
    su5.setUser(user1);
    assertThat(su1).isEqualTo(su1)
                   .isEqualTo(su2)
                   .hasSameHashCodeAs(su2)
                   .isNotEqualTo(su3)
                   .isNotEqualTo(su4)
                   .isNotEqualTo(su5)
                   .isNotEqualTo(null)
                   .isNotEqualTo(new Object());
  }

  @Test
  @SuppressWarnings({ "java:S5838", "java:S5863", "EqualsWithItself" })
  void testServerUserId() {
    var id1 = UUID.randomUUID();
    var su1 = new ServerUserId();
    su1.setServer(id1);
    su1.setUser(id1);
    var su2 = new ServerUserId();
    su2.setServer(id1);
    su2.setUser(id1);
    var su3 = new ServerUserId();
    su3.setServer(UUID.randomUUID());
    su3.setUser(UUID.randomUUID());
    var su4 = new ServerUserId();
    su4.setServer(id1);
    su4.setUser(UUID.randomUUID());
    var su5 = new ServerUserId();
    su5.setServer(UUID.randomUUID());
    su5.setUser(id1);
    assertThat(su1).isEqualTo(su1)
                   .isEqualTo(su2)
                   .hasSameHashCodeAs(su2)
                   .isNotEqualTo(su3)
                   .isNotEqualTo(su4)
                   .isNotEqualTo(su5)
                   .isNotEqualTo(null)
                   .isNotEqualTo(new Object());
  }
}