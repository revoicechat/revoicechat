package fr.revoicechat.core.service.server;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.model.room.RoomType;
import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.core.model.server.ServerCategory;
import fr.revoicechat.core.model.server.ServerRoomItem;
import fr.revoicechat.core.model.server.ServerStructure;
import fr.revoicechat.risk.service.server.ServerRoleDefaultCreator;
import fr.revoicechat.security.UserHolder;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Unremovable
@ApplicationScoped
public class NewServerCreator {

  private final EntityManager entityManager;
  private final UserHolder holder;
  private final ServerRoleDefaultCreator serverRoleCreator;

  public NewServerCreator(EntityManager entityManager, UserHolder holder, ServerRoleDefaultCreator serverRoleCreator) {
    this.entityManager = entityManager;
    this.holder = holder;
    this.serverRoleCreator = serverRoleCreator;
  }

  @Transactional
  public Server create(Server server) {
    server.setId(UUID.randomUUID());
    server.setOwner(holder.getOrNull());
    entityManager.persist(server);
    var general = createRoom(server, "General",  RoomType.TEXT);
    var random = createRoom(server, "Random",   RoomType.TEXT);
    var vocal = createRoom(server, "Vocal", RoomType.VOICE);
    server.setStructure(new ServerStructure(List.of(
        new ServerCategory("text", List.of(
            new ServerRoomItem(general.getId()),
            new ServerRoomItem(random.getId())
        )),
        new ServerCategory("vocal", List.of(new ServerRoomItem(vocal.getId())))
    )));
    entityManager.persist(server);
    serverRoleCreator.createDefault(server.getId());
    return server;
  }

  private ServerRoom createRoom(final Server server, final String name, RoomType type) {
    ServerRoom room = new ServerRoom();
    room.setId(UUID.randomUUID());
    room.setName(name);
    room.setServer(server);
    room.setType(type);
    entityManager.persist(room);
    return room;
  }
}
