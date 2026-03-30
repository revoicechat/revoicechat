package fr.revoicechat.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.revoicechat.core.model.room.ServerRoom;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestServerRoom {

  @Test
  @SuppressWarnings({ "java:S5838", "java:S5863", "EqualsWithItself" })
  void test() {
    var id1 = UUID.randomUUID();
    var room1 = new ServerRoom();
    room1.setId(id1);
    var room2 = new ServerRoom();
    room2.setId(id1);
    var room3 = new ServerRoom();
    room3.setId(UUID.randomUUID());

    assertThat(room1).isEqualTo(room1)
                     .isEqualTo(room2)
                     .hasSameHashCodeAs(room2)
                     .isNotEqualTo(room3)
                     .isNotEqualTo(null)
                     .isNotEqualTo(new Object());
  }
}