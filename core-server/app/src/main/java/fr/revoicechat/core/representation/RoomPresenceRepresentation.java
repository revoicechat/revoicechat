package fr.revoicechat.core.representation;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.live.stream.representation.StreamRepresentation;

/**
 * @param allUser Represent all the users that have access to a room.
 * @param connectedUser Represent all the users that are currently connected to a room.
 *                      If the room is a text room, this list is empty
 */
public record RoomPresenceRepresentation(
    UUID id,
    String name,
    List<UserRepresentation> allUser,
    List<ConnectedUserRepresentation> connectedUser
) {
  public record ConnectedUserRepresentation(UserRepresentation user,
                                            List<StreamRepresentation> streams) {}
}
