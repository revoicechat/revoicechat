package fr.revoicechat.core.service.message;

import static fr.revoicechat.core.nls.MessageErrorCode.*;

import java.util.Objects;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.technicaldata.media.NewMediaData;
import fr.revoicechat.core.technicaldata.message.NewMessage;
import fr.revoicechat.web.error.BadRequestException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class MessageValidation {

  private final int messageSize;
  private final EntityManager entityManager;

  public MessageValidation(@ConfigProperty(name = "revoicechat.message.max-length") int messageSize,
                           EntityManager entityManager) {
    this.messageSize = messageSize;
    this.entityManager = entityManager;
  }

  public void isValid(final UUID roomId, final NewMessage creation) {
    messageNotEmpty(creation);
    messageNotTooLong(creation);
    mediaMustHaveNames(creation);
    if (creation.answerTo() == null) {
      return;
    }
    var repliedMessage = entityManager.find(Message.class, creation.answerTo());
    repliedMessageMustExists(repliedMessage);
    repliedMessageMustBeInTheSameRoom(repliedMessage, roomId);
  }

  private void messageNotEmpty(NewMessage creation) {
    if (creation.text().isBlank() && creation.medias().isEmpty()) {
      throw new BadRequestException(MESSAGE_CANNOT_BE_EMPTY);
    }
  }

  private void messageNotTooLong(NewMessage creation) {
    if (creation.text().length() > messageSize) {
      throw new BadRequestException(MESSAGE_TOO_LONG, messageSize);
    }
  }

  private void mediaMustHaveNames(NewMessage creation) {
    if (creation.medias().stream().map(NewMediaData::name).anyMatch(String::isBlank)) {
      throw new BadRequestException(MEDIA_DATA_SHOULD_HAVE_A_NAME);
    }
  }

  private void repliedMessageMustExists(Message repliedMessage) {
    if (repliedMessage == null) {
      throw new BadRequestException(MESSAGE_ANSWERED_DOES_NOT_EXIST);
    }
  }

  private void repliedMessageMustBeInTheSameRoom(final Message repliedMessage, final UUID roomId) {
    if (!Objects.equals(roomId, repliedMessage.getRoom().getId())) {
      throw new BadRequestException(ANSWER_MUST_BE_IN_THE_SAME_ROOM);
    }
  }
}
