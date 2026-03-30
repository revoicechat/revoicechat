package fr.revoicechat.core.service.room;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import fr.revoicechat.core.model.room.Room;
import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.core.model.server.ServerCategory;
import fr.revoicechat.core.model.server.ServerItem;
import fr.revoicechat.core.model.server.ServerRoomItem;
import fr.revoicechat.core.model.server.ServerStructure;
import fr.revoicechat.core.nls.RoomErrorCode;
import fr.revoicechat.core.repository.RoomRepository;
import fr.revoicechat.core.risk.RoomRiskType;
import fr.revoicechat.core.service.server.ServerEntityService;
import fr.revoicechat.core.technicaldata.room.NewRoom;
import fr.revoicechat.risk.service.RiskService;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import fr.revoicechat.web.error.BadRequestException;
import fr.revoicechat.web.error.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * Service responsible for managing {@link ServerRoom} entities.
 * <p>
 * This service acts as a bridge between the application layer and the persistence layer,
 * providing methods to:
 * <ul>
 *     <li>Retrieve a list of all room</li>
 *     <li>Retrieve a single room by its ID</li>
 *     <li>Create new room</li>
 * </ul>
 * <p>
 */
@ApplicationScoped
public class RoomService {

  private final EntityManager entityManager;
  private final RoomRepository repository;
  private final ServerEntityService serverEntityService;
  private final RiskService riskService;

  public RoomService(EntityManager entityManager,
                     RoomRepository repository,
                     ServerEntityService serverEntityService,
                     RiskService riskService) {
    this.entityManager = entityManager;
    this.repository = repository;
    this.serverEntityService = serverEntityService;
    this.riskService = riskService;
  }

  /**
   * Retrieves all available rooms of a server.
   * @return a list of available rooms, possibly empty
   */
  public List<ServerRoom> findAll(final UUID id) {
    return repository.findByServerId(id);
  }

  /**
   * Retrieves all available rooms of a server.
   * @return a list of available rooms, possibly empty
   */
  public List<ServerRoom> findAllForCurrentUser(final UUID id) {
    return repository.findByServerId(id)
                     .stream()
                     .filter(room -> riskService.hasRisk(new RiskEntity(id, room.getId()), RoomRiskType.SERVER_ROOM_READ))
                     .toList();
  }

  /**
   * Creates and stores a new room in the database.
   * @param id       the server id
   * @param creation the room entity to persist
   * @return the persisted room entity with its generated ID
   * @throws ResourceNotFoundException if no server with the given ID exists
   */
  @Transactional
  public Room create(final UUID id, final NewRoom creation) {
    var room = new ServerRoom();
    room.setId(UUID.randomUUID());
    room.setType(creation.type());
    room.setName(creation.name());
    var server = serverEntityService.getEntity(id);
    room.setServer(server);
    entityManager.persist(room);
    var structure = new ServerStructure(new ArrayList<>());
    structure.items().addAll(server.getStructure().items());
    structure.items().add(new ServerRoomItem(room.getId()));
    return room;
  }

  /**
   * Update and stores a room in the database.
   * @param id       the room id
   * @param creation the room entity to persist
   * @return the persisted room entity
   * @throws ResourceNotFoundException if no server with the given ID exists
   */
  @Transactional
  public Room update(final UUID id, final NewRoom creation) {
    var room = getRoom(id);
    if (creation.type() != null && room.getType() != creation.type()) {
      throw new BadRequestException(RoomErrorCode.ROOM_TYPE_CANNOT_BE_CHANGED);
    }
    room.setName(creation.name());
    entityManager.persist(room);
    return room;
  }

  /**
   * Creates and stores a new room in the database.
   * @param id the server id
   * @return the persisted room entity with its generated ID
   * @throws ResourceNotFoundException if no server with the given ID exists
   */
  @Transactional
  public Room delete(final UUID id) {
    var room = getRoom(id);
    if (room instanceof ServerRoom serverRoom) {
      var server = serverRoom.getServer();
      server.setStructure(removeFromStructure(server.getStructure(), serverRoom));
      entityManager.persist(server);
    }
    Optional.ofNullable(entityManager.find(Room.class, id)).ifPresent(entityManager::remove);
    return room;
  }

  /**
   * Retrieves a room from the database by its unique identifier.
   * @param roomId the unique room ID
   * @return the room entity
   * @throws ResourceNotFoundException if no room with the given ID exists
   */
  public Room getRoom(final UUID roomId) {
    return Optional.ofNullable(entityManager.find(Room.class, roomId)).orElseThrow(() -> new ResourceNotFoundException(Room.class, roomId));
  }

  private ServerStructure removeFromStructure(ServerStructure structure, ServerRoom room) {
    return new ServerStructure(
        structure.items().stream()
                 .map(i -> removeFromStructure(i, room.getId()))
                 .filter(Objects::nonNull)
                 .toList());
  }

  private ServerItem removeFromStructure(ServerItem item, UUID roomId) {
    return switch (item) {
      case ServerRoomItem room when room.id().equals(roomId) -> null;
      case ServerCategory(String name, List<ServerItem> items) -> new ServerCategory(name, items.stream()
                                                                                                .map(i -> removeFromStructure(i, roomId))
                                                                                                .filter(Objects::nonNull)
                                                                                                .toList());
      case null, default -> item;
    };
  }
}
