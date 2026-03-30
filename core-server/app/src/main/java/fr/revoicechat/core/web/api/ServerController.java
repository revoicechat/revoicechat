package fr.revoicechat.core.web.api;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import fr.revoicechat.core.representation.RoomRepresentation;
import fr.revoicechat.core.representation.ServerRepresentation;
import fr.revoicechat.core.representation.UserRepresentation;
import fr.revoicechat.core.technicaldata.room.NewRoom;
import fr.revoicechat.core.technicaldata.server.NewServer;
import fr.revoicechat.openapi.api.LoggedApi;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("server")
@Tag(name = "Server", description = "Manage servers and their associated rooms")
public interface ServerController extends LoggedApi {

  @Operation(summary = "Get all servers", description = "Retrieve a list of all servers the authenticated user has access to.")
  @APIResponse(responseCode = "200", description = "Server list retrieved successfully")
  @GET
  List<ServerRepresentation> getServers();

  @Operation(summary = "Get all servers", description = "Retrieve a list of all servers the authenticated user has access to.")
  @APIResponse(responseCode = "200", description = "Server list retrieved successfully")
  @GET
  @Path("all")
  List<ServerRepresentation> getAllServers();

  @Operation(summary = "Get all servers", description = "Retrieve a list of all servers the authenticated user has access to.")
  @APIResponse(responseCode = "200", description = "Server list retrieved successfully")
  @GET
  @Path("/discover")
  List<ServerRepresentation> getPublicServers(@QueryParam("joinedToo")
                                              @DefaultValue("false") boolean joinedToo);

  @Operation(summary = "Get server by ID", description = "Retrieve detailed information about a specific server. Users must have access to the server to view its details.")
  @APIResponse(responseCode = "200", description = "Server retrieved successfully")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to access this server")
  @APIResponse(responseCode = "404", description = "Server not found")
  @GET
  @Path("/{id}")
  ServerRepresentation getServer(@PathParam("id") UUID id);

  @Operation(summary = "Create server", description = "Create a new server with the specified configuration. The authenticated user becomes the server owner.")
  @APIResponse(responseCode = "200", description = "Server created successfully")
  @APIResponse(responseCode = "400", description = "Invalid server data provided")
  @PUT
  ServerRepresentation createServer(NewServer representation);

  @Operation(summary = "Update server", description = "Update the properties of an existing server such as name, description, or icon. Requires server administrative permissions.")
  @APIResponse(responseCode = "200", description = "Server updated successfully")
  @APIResponse(responseCode = "400", description = "Invalid server data provided")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to update this server")
  @APIResponse(responseCode = "404", description = "Server not found")
  @PATCH
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{id}")
  ServerRepresentation updateServer(@PathParam("id") UUID id, NewServer representation);

  @Operation(summary = "Delete server", description = "Permanently delete a server and all associated rooms and messages. This action cannot be undone. Only the server owner can delete a server.")
  @APIResponse(responseCode = "200", description = "Server deleted successfully")
  @APIResponse(responseCode = "400", description = "Server cannot be deleted")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to delete this server")
  @APIResponse(responseCode = "404", description = "Server not found")
  @DELETE
  @Path("/{id}")
  void deleteServer(@PathParam("id") UUID id);

  @Tags(refs = { "Server", "Room" })
  @Operation(summary = "Get server rooms", description = "Retrieve all rooms belonging to a specific server. Users must have access to the server to view its rooms.")
  @APIResponse(responseCode = "200", description = "Room list retrieved successfully")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to access this server")
  @APIResponse(responseCode = "404", description = "Server not found")
  @GET
  @Path("/{id}/room")
  List<RoomRepresentation> getRooms(@PathParam("id") final UUID id);

  @Tags(refs = { "Server", "Room" })
  @Operation(summary = "Create room in server", description = "Create a new room within a specific server. Requires server administrative permissions.")
  @APIResponse(responseCode = "200", description = "Room created successfully")
  @APIResponse(responseCode = "400", description = "Invalid room data provided")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to create rooms in this server")
  @APIResponse(responseCode = "404", description = "Server not found")
  @PUT
  @Path("/{id}/room")
  RoomRepresentation createRoom(@PathParam("id") final UUID id, NewRoom representation);

  @Tags(refs = { "Server", "User" })
  @Operation(summary = "Get server members", description = "Retrieve the list of all users who are members of a specific server.")
  @APIResponse(responseCode = "200", description = "Member list retrieved successfully")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to access this server")
  @APIResponse(responseCode = "404", description = "Server not found")
  @GET
  @Path("/{id}/user")
  List<UserRepresentation> fetchUsers(@PathParam("id") UUID id);
}
