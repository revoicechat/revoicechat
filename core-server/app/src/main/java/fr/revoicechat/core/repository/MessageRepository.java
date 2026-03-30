package fr.revoicechat.core.repository;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.core.repository.page.PageResult;
import fr.revoicechat.core.technicaldata.message.MessageFilterParams;

public interface MessageRepository {
  PageResult<Message> search(MessageFilterParams params);

  Stream<Message> findByRoom(ServerRoom room);

  Message findByMedia(UUID mediaId);
}
