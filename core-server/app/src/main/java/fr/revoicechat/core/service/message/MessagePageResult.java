package fr.revoicechat.core.service.message;

import java.util.UUID;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.repository.impl.MessageRepositoryImpl;
import fr.revoicechat.core.repository.page.PageResult;
import fr.revoicechat.core.technicaldata.message.MessageFilterParams;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MessagePageResult {

  private final MessageRepositoryImpl messageRepositoryImpl;

  public MessagePageResult(final MessageRepositoryImpl messageRepositoryImpl) {
    this.messageRepositoryImpl = messageRepositoryImpl;
  }

  /**
   * Retrieves all messages for a given chat room.
   *
   * @param roomId the unique identifier of the chat room
   * @return list of messages in the room, possibly empty if no messages exist
   */
  @Transactional
  public PageResult<Message> getMessagesByRoom(UUID roomId, MessageFilterParams params) {
    params.setRoomId(roomId);
    return messageRepositoryImpl.search(params);
  }
}
