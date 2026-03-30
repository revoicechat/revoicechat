package fr.revoicechat.core.service.message;

import static fr.revoicechat.core.nls.MessageErrorCode.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.core.technicaldata.media.NewMediaData;
import fr.revoicechat.core.technicaldata.message.NewMessage;
import fr.revoicechat.core.stub.EntityManagerMock;
import fr.revoicechat.web.error.BadRequestException;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestMessageValidation {

  private final InstanceFinderMock entityManager = new InstanceFinderMock();
  private final MessageValidation validation = new MessageValidation(10, entityManager);

  @BeforeEach
  void setUp() {
    entityManager.message = null;
  }

  public static Stream<Arguments> messageTest() {
    return Stream.of(
        Arguments.of("", MESSAGE_CANNOT_BE_EMPTY.translate()),
        Arguments.of("0123456789+", MESSAGE_TOO_LONG.translate(10)),
        Arguments.of("0123456789", null)
    );
  }

  @ParameterizedTest
  @MethodSource("messageTest")
  void testMessage(String message, String errorMessage) {
    NewMessage creation = new NewMessage(message, null, List.of());
    var ex = Assertions.catchException(() -> validation.isValid(null, creation));
    if (errorMessage != null) {
      Assertions.assertThat(ex).isInstanceOf(BadRequestException.class).hasMessage(errorMessage);
    } else {
      Assertions.assertThat(ex).isNull();
    }
  }

  public static Stream<Arguments> mediaTest() {
    return Stream.of(
        Arguments.of("", MEDIA_DATA_SHOULD_HAVE_A_NAME.translate()),
        Arguments.of("test", null)
    );
  }

  @ParameterizedTest
  @MethodSource("mediaTest")
  void testMedia(String mediaName, String errorMessage) {
    NewMessage creation = new NewMessage("", null, List.of(new NewMediaData("test"), new NewMediaData(mediaName)));
    var ex = Assertions.catchException(() -> validation.isValid(null, creation));
    if (errorMessage != null) {
      Assertions.assertThat(ex).isInstanceOf(BadRequestException.class).hasMessage(errorMessage);
    } else {
      Assertions.assertThat(ex).isNull();
    }
  }

  @Test
  void testAnswerDoesNotExist() {
    NewMessage creation = new NewMessage("test", UUID.randomUUID(), List.of());
    entityManager.message = null;
    var ex = Assertions.catchException(() -> validation.isValid(null, creation));
    Assertions.assertThat(ex).isInstanceOf(BadRequestException.class).hasMessage(MESSAGE_ANSWERED_DOES_NOT_EXIST.translate());
  }

  @Test
  void testAnswerNotInSameRoom() {
    NewMessage creation = new NewMessage("test", UUID.randomUUID(), List.of());
    entityManager.message = new Message();
    entityManager.message.setRoom(new ServerRoom());
    entityManager.message.getRoom().setId(UUID.randomUUID());
    var ex = Assertions.catchException(() -> validation.isValid(UUID.randomUUID(), creation));
    Assertions.assertThat(ex).isInstanceOf(BadRequestException.class).hasMessage(ANSWER_MUST_BE_IN_THE_SAME_ROOM.translate());
  }

  @Test
  void testAnswerNoError() {
    var roomId = UUID.randomUUID();
    NewMessage creation = new NewMessage("test", UUID.randomUUID(), List.of());
    entityManager.message = new Message();
    entityManager.message.setRoom(new ServerRoom());
    entityManager.message.getRoom().setId(roomId);
    var ex = Assertions.catchException(() -> validation.isValid(roomId, creation));
    Assertions.assertThat(ex).isNull();
  }

  private static class InstanceFinderMock extends EntityManagerMock {
    Message message;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T find(final Class<T> aClass, final Object o) {
      return (T) message;
    }
  }
}