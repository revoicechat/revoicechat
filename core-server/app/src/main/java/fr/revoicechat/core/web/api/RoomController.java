package fr.revoicechat.core.web.api;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import fr.revoicechat.core.repository.page.PageResult;
import fr.revoicechat.core.representation.MessageRepresentation;
import fr.revoicechat.core.representation.RoomPresenceRepresentation;
import fr.revoicechat.core.representation.RoomRepresentation;
import fr.revoicechat.core.technicaldata.message.MessageFilterParams;
import fr.revoicechat.core.technicaldata.message.NewMessage;
import fr.revoicechat.core.technicaldata.room.NewRoom;
import fr.revoicechat.openapi.api.LoggedApi;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("room/{id}")
@Tag(name = "Room", description = "Manage chat rooms and their messages")
public interface RoomController extends LoggedApi {

  @Operation(
      summary = "Get room details",
      description = "Retrieve complete information about a specific room including its properties and configuration. Users must have access to the server containing this room."
  )
  @APIResponse(responseCode = "200", description = "Room retrieved successfully")
  @APIResponse(
      responseCode = "403",
      description = "Insufficient permissions to access this room"
  )
  @APIResponse(
      responseCode = "404",
      description = "Room not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Room not found")
      )
  )
  @GET
  RoomRepresentation read(@PathParam("id") UUID roomId);

  @Operation(
      summary = "Update room",
      description = "Update the properties of an existing room such as name, description, or settings. Requires appropriate permissions."
  )
  @APIResponse(responseCode = "200", description = "Room updated successfully")
  @APIResponse(responseCode = "400", description = "Invalid room data provided")
  @APIResponse(
      responseCode = "403",
      description = "Insufficient permissions to update this room"
  )
  @APIResponse(
      responseCode = "404",
      description = "Room not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Room not found")
      )
  )
  @PATCH
  RoomRepresentation update(@PathParam("id") UUID roomId, NewRoom representation);

  @Operation(
      summary = "Delete room",
      description = "Permanently delete a room and all its messages. This action cannot be undone. Requires administrative permissions."
  )
  @APIResponse(responseCode = "200", description = "Room deleted successfully")
  @APIResponse(
      responseCode = "403",
      description = "Insufficient permissions to delete this room"
  )
  @APIResponse(
      responseCode = "404",
      description = "Room not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Room not found")
      )
  )
  @DELETE
  UUID delete(@PathParam("id") UUID roomId);

  @Tags(refs = { "Room", "Message" })
  @Operation(summary = "Get room messages", description = "Retrieve a paginated list of messages from a specific room, ordered by timestamp (newest first).")
  @APIResponse(responseCode = "200", description = "Messages retrieved successfully")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to access messages in this room")
  @APIResponse(responseCode = "404", description = "Room not found")
  @GET
  @Path("/message")
  PageResult<MessageRepresentation> messages(
      @PathParam("id") UUID roomId,
      @BeanParam MessageFilterParams params
  );

  @Tags(refs = { "Room", "Message" })
  @Operation(
      summary = "Send message",
      description = "Create and send a new message in a specific room. Users must have access to the room to send messages."
  )
  @APIResponse(responseCode = "200", description = "Message sent successfully")
  @APIResponse(responseCode = "400", description = "Invalid message data provided")
  @APIResponse(
      responseCode = "403",
      description = "Insufficient permissions to send messages in this room"
  )
  @APIResponse(
      responseCode = "404",
      description = "Room not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Room not found")
      )
  )
  @PUT
  @Path("/message")
  MessageRepresentation sendMessage(@PathParam("id") UUID roomId, NewMessage representation);

  @Tags(refs = { "Room", "User" })
  @Operation(
      summary = "Get room users",
      description = "Retrieve the list of users currently present or with access to a specific room, including their online status."
  )
  @APIResponse(responseCode = "200", description = "User list retrieved successfully")
  @APIResponse(
      responseCode = "403",
      description = "Insufficient permissions to view users in this room"
  )
  @APIResponse(
      responseCode = "404",
      description = "Room not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Room not found")
      )
  )
  @GET
  @Path("/user")
  RoomPresenceRepresentation fetchUsers(@PathParam("id") UUID id);
}
