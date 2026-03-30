package fr.revoicechat.core.web.api;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("room/{id}/read-status")
@Tag(name = "Room", description = "Manage chat rooms status")
public interface RoomReadStatusController {
  @PUT
  void markAsRead(@PathParam("id") UUID roomId, UUID lastMessageId);
}
