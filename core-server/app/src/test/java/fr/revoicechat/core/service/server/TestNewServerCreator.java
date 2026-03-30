package fr.revoicechat.core.service.server;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.core.model.room.RoomType;
import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.model.User;
import fr.revoicechat.core.stub.EntityManagerMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@ExtendWith({ SoftAssertionsExtension.class })
class TestNewServerCreator {

  @Test
  void test(SoftAssertions softly) {
    // Given
    try (var em = new MockEntityManager()) {
      Server server = new Server();
      // When
      new NewServerCreator(em, new UserHolderMock<>(new User()), _ -> {}).create(server);
      // Then
      softly.assertThat(server.getId()).isNotNull();
      assertThat(em.saved).hasSize(5);
      assertThat(em.saved.get(0)).isInstanceOf(Server.class);
      ServerRoom room1 = (ServerRoom) em.saved.get(1);
      assertRoom(softly, room1, "General", server, RoomType.TEXT);
      ServerRoom room2 = (ServerRoom) em.saved.get(2);
      assertRoom(softly, room2, "Random", server, RoomType.TEXT);
      ServerRoom room3 = (ServerRoom) em.saved.get(3);
      assertRoom(softly, room3, "Vocal", server, RoomType.VOICE);
      assertThat(em.saved.get(4)).isInstanceOf(Server.class);
    }
  }

  private static void assertRoom(final SoftAssertions softly, final ServerRoom room1, final String General, final Server server, final RoomType text) {
    softly.assertThat(room1.getId()).isNotNull();
    softly.assertThat(room1.getName()).isEqualTo(General);
    softly.assertThat(room1.getServer()).isEqualTo(server);
    softly.assertThat(room1.getType()).isEqualTo(text);
  }

  private static class MockEntityManager extends EntityManagerMock {
    private final List<Object> saved = new ArrayList<>();

    @Override
    public void persist(final Object o) {
      saved.add(o);
    }
  }
}