package fr.revoicechat.core.web.api;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import fr.revoicechat.core.representation.EmoteRepresentation;
import fr.revoicechat.core.technicaldata.emote.NewEmote;
import fr.revoicechat.openapi.api.LoggedApi;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("emote")
@Tag(name = "Emote", description = "Manage custom emotes for users, servers, and the application")
public interface EmoteController extends LoggedApi {

  @Operation(
      summary = "Get personal emotes",
      description = "Retrieve all emotes owned by the currently authenticated user."
  )
  @APIResponse(responseCode = "200", description = "Emotes retrieved successfully")
  @GET
  @Path("/me")
  List<EmoteRepresentation> getMyEmotes();

  @Operation(
      summary = "Add personal emote",
      description = "Create a new emote and add it to the currently authenticated user's collection."
  )
  @APIResponse(responseCode = "200", description = "Emote created and added successfully")
  @APIResponse(responseCode = "400", description = "Invalid emote data provided")
  @PUT
  @Path("/me")
  EmoteRepresentation addToMyEmotes(NewEmote emote);

  @Operation(
      summary = "Get global emotes",
      description = "Retrieve all emotes available globally across the entire application."
  )
  @APIResponse(responseCode = "200", description = "Global emotes retrieved successfully")
  @GET
  @Path("/global")
  List<EmoteRepresentation> getGlobalEmotes();

  @Operation(
      summary = "Add global emote",
      description = "Create a new emote and make it available globally across the entire application. Requires appropriate permissions."
  )
  @APIResponse(responseCode = "200", description = "Global emote created successfully")
  @APIResponse(responseCode = "400", description = "Invalid emote data provided")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to create global emotes")
  @PUT
  @Path("/global")
  EmoteRepresentation addToGlobalEmotes(NewEmote emote);

  @Operation(
      summary = "Get server emotes",
      description = "Retrieve all emotes available within a specific server."
  )
  @APIResponse(responseCode = "200", description = "Server emotes retrieved successfully")
  @APIResponse(responseCode = "404", description = "Server not found")
  @GET
  @Path("/server/{id}")
  List<EmoteRepresentation> getServerEmotes(@PathParam("id") UUID serverId);

  @Operation(
      summary = "Add server emote",
      description = "Create a new emote and add it to a specific server's collection. Requires appropriate server permissions."
  )
  @APIResponse(responseCode = "200", description = "Server emote created successfully")
  @APIResponse(responseCode = "400", description = "Invalid emote data provided")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to manage server emotes")
  @APIResponse(responseCode = "404", description = "Server not found")
  @PUT
  @Path("/server/{id}")
  EmoteRepresentation addToServerEmotes(@PathParam("id") UUID serverId, NewEmote emote);

  @Operation(
      summary = "Get emote by ID",
      description = "Retrieve detailed information about a specific emote."
  )
  @APIResponse(responseCode = "200", description = "Emote retrieved successfully")
  @APIResponse(responseCode = "404", description = "Emote not found")
  @GET
  @Path("/{id}")
  EmoteRepresentation getEmote(@PathParam("id") UUID id);

  @Operation(
      summary = "Update emote",
      description = "Update an existing emote's properties. Only the emote owner or administrators can perform this action."
  )
  @APIResponse(responseCode = "200", description = "Emote updated successfully")
  @APIResponse(responseCode = "400", description = "Invalid emote data provided")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to update this emote")
  @APIResponse(responseCode = "404", description = "Emote not found")
  @PATCH
  @Path("/{id}")
  EmoteRepresentation patchEmote(@PathParam("id") UUID id, NewEmote emote);

  @Operation(
      summary = "Delete emote",
      description = "Permanently delete an emote. Only the emote owner or administrators can perform this action."
  )
  @APIResponse(responseCode = "200", description = "Emote deleted successfully")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to delete this emote")
  @APIResponse(responseCode = "404", description = "Emote not found")
  @DELETE
  @Path("/{id}")
  void deleteEmote(@PathParam("id") UUID id);
}
