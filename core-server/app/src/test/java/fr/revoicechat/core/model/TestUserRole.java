package fr.revoicechat.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.revoicechat.core.model.UserRole.UserRolePK;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestUserRole {

  @Test
  @SuppressWarnings({ "java:S5838", "java:S5863", "EqualsWithItself" })
  void test() {
    var id1 = UUID.randomUUID();
    var user1 = user(id1);
    var user2 = user(id1);
    var user3 = user(UUID.randomUUID());

    var id2 = UUID.randomUUID();
    var server1 = server(id2);
    var server2 = server(id2);
    var server3 = server(UUID.randomUUID());

    var role1 = userRole(user1, server1);
    var role2 = userRole(user1, server2);
    var role3 = userRole(user1, server3);
    var role4 = userRole(user2, server1);
    var role5 = userRole(user2, server2);
    var role6 = userRole(user2, server3);
    var role7 = userRole(user3, server1);
    var role8 = userRole(user3, server2);
    var role9 = userRole(user3, server3);

    assertThat(role1).isEqualTo(role1)
                     .isEqualTo(role2)
                     .hasSameHashCodeAs(role2)
                     .isNotEqualTo(role3)
                     .isEqualTo(role4)
                     .isEqualTo(role5)
                     .isNotEqualTo(role6)
                     .isNotEqualTo(role7)
                     .isNotEqualTo(role8)
                     .isNotEqualTo(role9)
                     .isNotEqualTo(null)
                     .isNotEqualTo(new Object());
    assertThat(role1.getPk()).isEqualTo(role1.getPk())
                             .isNotEqualTo(null)
                             .isNotEqualTo(new Object());

  }

  private static User user(final UUID id1) {
    var user1 = new User();
    user1.setId(id1);
    return user1;
  }

  private static Server server(final UUID id2) {
    var server1 = new Server();
    server1.setId(id2);
    return server1;
  }

  private static UserRole userRole(final User user1, final Server server1) {
    var role1 = new UserRole();
    role1.setPk(new UserRolePK());
    role1.setUser(user1);
    role1.setServer(server1);
    return role1;
  }


}