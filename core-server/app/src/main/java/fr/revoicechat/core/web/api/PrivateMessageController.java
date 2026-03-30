package fr.revoicechat.core.web.api;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import fr.revoicechat.core.repository.page.PageResult;
import fr.revoicechat.core.representation.MessageRepresentation;
import fr.revoicechat.core.representation.RoomRepresentation;
import fr.revoicechat.core.technicaldata.message.MessageFilterParams;
import fr.revoicechat.core.technicaldata.message.NewMessage;
import fr.revoicechat.openapi.api.LoggedApi;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("private-message")
@Tag(name = "Private Message", description = "Manage private messages")
public interface PrivateMessageController extends LoggedApi {

  @Operation(summary = "Get all Private message discussion")
  @GET
  List<RoomRepresentation> findAll();

  @Operation(summary = "Get specific private message discussion")
  @GET
  @Path("{id}")
  RoomRepresentation get(@PathParam("id") UUID id);

  @Tags(refs = { "Private Message", "Message" })
  @Operation(summary = "Get private message")
  @APIResponse(responseCode = "200", description = "Messages retrieved successfully")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to access messages in this room")
  @APIResponse(responseCode = "404", description = "Room not found")
  @GET
  @Path("{id}/message")
  PageResult<MessageRepresentation> messages(
      @PathParam("id") UUID roomId,
      @BeanParam MessageFilterParams params
  );

  @Tags(refs = { "Private Message", "Message" })
  @Operation(summary = "Send message")
  @APIResponse(responseCode = "200", description = "Message sent successfully")
  @APIResponse(responseCode = "400", description = "Invalid message data provided")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to send messages in this room")
  @APIResponse(responseCode = "404", description = "Room not found")
  @PUT
  @Path("{id}/message")
  MessageRepresentation sendMessage(@PathParam("id") UUID roomId, NewMessage representation);

}
