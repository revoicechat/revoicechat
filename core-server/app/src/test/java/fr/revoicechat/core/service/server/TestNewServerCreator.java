package fr.revoicechat.core.service.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.model.room.RoomType;
import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.core.service.user.UserRetriever;
import fr.revoicechat.security.model.AuthenticatedUser;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.persistence.EntityManager;

@QuarkusTest
@ExtendWith({ MockitoExtension.class, SoftAssertionsExtension.class })
class TestNewServerCreator {

  @Mock
  private EntityManager em;

  @Captor
  private ArgumentCaptor<Object> savedCaptor;

  @Test
  void test(SoftAssertions softly) {
    // Given
    Server server = new Server();
    UserRetriever userRetriever = new UserRetriever(new UserHolderMock(new AuthenticatedUser()), em);
    NewServerCreator creator = new NewServerCreator(em, userRetriever, _ -> {});
    // When
    creator.create(server);
    // Then
    verify(em, times(5)).persist(savedCaptor.capture());
    List<Object> saved = savedCaptor.getAllValues();
    softly.assertThat(server.getId()).isNotNull();
    softly.assertThat(saved.get(0)).isInstanceOf(Server.class);
    assertRoom(softly, saved.get(1), "General", server, RoomType.TEXT);
    assertRoom(softly, saved.get(2), "Random", server, RoomType.TEXT);
    assertRoom(softly, saved.get(3), "Vocal", server, RoomType.VOICE);
    softly.assertThat(saved.get(4)).isInstanceOf(Server.class);
  }

  private static void assertRoom(final SoftAssertions softly, final Object saved, final String name,
                                 final Server server, final RoomType type) {
    assertThat(saved).isInstanceOf(ServerRoom.class);
    ServerRoom room = (ServerRoom) saved;
    softly.assertThat(room.getId()).isNotNull();
    softly.assertThat(room.getName()).isEqualTo(name);
    softly.assertThat(room.getServer()).isEqualTo(server);
    softly.assertThat(room.getType()).isEqualTo(type);
  }
}