package fr.revoicechat.core.web.api;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import fr.revoicechat.core.representation.MessageRepresentation;
import fr.revoicechat.core.technicaldata.message.NewMessage;
import fr.revoicechat.openapi.api.LoggedApi;
import fr.revoicechat.opengraph.OpenGraphSchema;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("message/{id}")
@Tag(name = "Message", description = "Manage individual chat messages")
public interface MessageController extends LoggedApi {

  @Operation(
      summary = "Get message by ID",
      description = "Retrieve a specific message by its unique identifier. Users must have access to the room containing this message."
  )
  @APIResponse(responseCode = "200", description = "Message retrieved successfully")
  @APIResponse(
      responseCode = "403",
      description = "Insufficient permissions to access this message"
  )
  @APIResponse(
      responseCode = "404",
      description = "Message not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Message not found")
      )
  )
  @GET
  MessageRepresentation read(@PathParam("id") UUID id);

  @Operation(
      summary = "Get OpenGraph by message ID",
      description = "Retrieve a specific OpenGraph by its message unique identifier. Users must have access to the room containing this message."
  )
  @APIResponse(responseCode = "200", description = "OpenGraph of a message successfully extract")
  @GET
  @Path("/open-graph")
  OpenGraphSchema getOpenGraph(@PathParam("id") UUID id);

  @Operation(
      summary = "Update message",
      description = "Update the content of an existing message. Only the message author can update their own messages within a limited time window."
  )
  @APIResponse(responseCode = "200", description = "Message updated successfully")
  @APIResponse(responseCode = "400", description = "Invalid message data provided")
  @APIResponse(
      responseCode = "403",
      description = "Insufficient permissions to update this message or edit window has expired"
  )
  @APIResponse(
      responseCode = "404",
      description = "Message not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Message not found")
      )
  )
  @PATCH
  MessageRepresentation update(@PathParam("id") UUID id, NewMessage representation);

  @Operation(
      summary = "Delete message",
      description = "Permanently delete a message. Message authors can delete their own messages, and administrators can delete any message."
  )
  @APIResponse(responseCode = "204", description = "Message deleted successfully")
  @APIResponse(
      responseCode = "403",
      description = "Insufficient permissions to delete this message"
  )
  @APIResponse(
      responseCode = "404",
      description = "Message not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Message not found")
      )
  )
  @DELETE
  UUID delete(@PathParam("id") UUID id);

  @APIResponse(responseCode = "200", description = "Message reaction added successfully")
  @APIResponse(responseCode = "400", description = "Invalid message data provided")
  @APIResponse(responseCode = "404", description = "Message not found")
  @POST
  @Path("reaction/{emoji}")
  MessageRepresentation addReaction(@PathParam("id") UUID id, @PathParam("emoji") String emoji);
}
