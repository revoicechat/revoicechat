package fr.revoicechat.core.service.message;

import static fr.revoicechat.core.model.MediaOrigin.ATTACHMENT;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.model.MessageReactions;
import fr.revoicechat.core.repository.MessageRepository;
import fr.revoicechat.core.risk.MessageRiskType;
import fr.revoicechat.core.service.media.MediaDataService;
import fr.revoicechat.core.service.room.RoomService;
import fr.revoicechat.core.technicaldata.message.NewMessage;
import fr.revoicechat.security.UserHolder;
import fr.revoicechat.web.error.ResourceNotFoundException;

/**
 * Service layer for managing chat messages within rooms.
 * <p>
 * This service provides operations to create, retrieve, update, and delete messages.
 * It acts as an intermediary between the {@link MessageRepository} (data access layer)
 * and higher-level components such as controllers or WebSocket endpoints.
 * </p>
 * <p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Manage message persistence for specific chat rooms</li>
 *   <li>Notify clients about message events via the textual chat service</li>
 * </ul>
 *
 * @see MessageRepository
 * @see RoomService
 */
@ApplicationScoped
public class MessageService {

  private final EntityManager entityManager;
  private final RoomService roomService;
  private final UserHolder userHolder;
  private final MessageValidation messageValidation;
  private final MediaDataService mediaDataService;
  private final MessageAuthorization messageAuthorization;

  public MessageService(EntityManager entityManager,
                        RoomService roomService,
                        UserHolder userHolder,
                        MessageValidation messageValidation,
                        MediaDataService mediaDataService,
                        MessageAuthorization messageAuthorization) {
    this.entityManager = entityManager;
    this.roomService = roomService;
    this.userHolder = userHolder;
    this.messageValidation = messageValidation;
    this.mediaDataService = mediaDataService;
    this.messageAuthorization = messageAuthorization;
  }

  /**
   * Creates and persists a new message in the specified chat room.
   * Also notifies connected clients about the new message.
   *
   * @param roomId   the unique identifier of the chat room where the message will be added
   * @param newMessage the message data to create
   * @return the created message
   */
  @Transactional
  public Message create(UUID roomId, NewMessage newMessage) {
    messageValidation.isValid(roomId, newMessage);
    var room = roomService.getRoom(roomId);
    var message = new Message();
    message.setId(UUID.randomUUID());
    message.setText(newMessage.text());
    message.setCreatedDate(OffsetDateTime.now());
    message.setCreatedDate(OffsetDateTime.now());
    message.setReactions(new MessageReactions(new ArrayList<>()));
    message.setRoom(room);
    Optional.ofNullable(newMessage.answerTo())
            .map(this::getMessage)
            .ifPresent(message::setAnswerTo);
    message.setUser(userHolder.get());
    newMessage.medias().stream().map(data -> mediaDataService.create(data, ATTACHMENT)).forEach(message::addMediaData);
    entityManager.persist(message);
    return message;
  }

  /**
   * Updates the content of an existing message and notifies connected clients.
   *
   * @param id       the unique identifier of the message to update
   * @param creation the new message content
   * @return the updated message
   * @throws ResourceNotFoundException if the message does not exist
   */
  @Transactional
  public Message update(UUID id, NewMessage creation) {
    var message = getMessage(id);
    messageAuthorization.asserRisk(message, MessageRiskType.MESSAGE_UPDATE);
    messageValidation.isValid(message.getRoom().getId(), creation);
    message.setText(creation.text());
    message.setUpdatedDate(OffsetDateTime.now());
    entityManager.persist(message);
    return message;
  }

  /**
   * Deletes a message from the repository and notifies connected clients.
   *
   * @param id the unique identifier of the message to delete
   * @return the UUID of the deleted message
   * @throws ResourceNotFoundException if the message does not exist
   */
  @Transactional
  public Message delete(UUID id) {
    var message = getMessage(id);
    messageAuthorization.asserRisk(message, MessageRiskType.MESSAGE_DELETE);
    entityManager.remove(message);
    return message;
  }

  @Transactional
  public Message addReaction(final UUID id, final String emoji) {
    var message = getMessage(id);
    var user = userHolder.get().getId();
    message.setReactions(message.getReactions().toggle(emoji, user));
    entityManager.persist(message);
    return message;
  }

  /**
   * Retrieves the details of a specific message.
   *
   * @param id the unique identifier of the message
   * @return the message
   * @throws ResourceNotFoundException if the message does not exist
   */
  @Transactional
  public Message getMessage(final UUID id) {
    return Optional.ofNullable(entityManager.find(Message.class, id))
                   .orElseThrow(() -> new ResourceNotFoundException(Message.class, id));
  }
}
