package fr.revoicechat.risk.web.api;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import fr.revoicechat.risk.representation.CreatedServerRoleRepresentation;
import fr.revoicechat.risk.representation.ServerRoleRepresentation;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("server/{id}/role")
@Tag(name = "Role", description = "Manage server roles and permissions")
public interface ServerRoleController {

  @Operation(
      summary = "Get server roles",
      description = "Retrieve all roles configured for a specific server, including their permissions and hierarchy. Users must have access to the server to view its roles."
  )
  @APIResponse(responseCode = "200", description = "Server roles retrieved successfully")
  @APIResponse(
      responseCode = "403",
      description = "Insufficient permissions to access this server"
  )
  @APIResponse(
      responseCode = "404",
      description = "Server not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Server not found")
      )
  )
  @GET
  List<ServerRoleRepresentation> getByServer(@PathParam("id") UUID serverId);

  @Operation(
      summary = "Create server role",
      description = "Create a new role within a specific server with custom permissions and settings. Requires server administrative permissions."
  )
  @APIResponse(responseCode = "200", description = "Role created successfully")
  @APIResponse(responseCode = "400", description = "Invalid role data provided")
  @APIResponse(
      responseCode = "403",
      description = "Insufficient permissions to create roles in this server"
  )
  @APIResponse(
      responseCode = "404",
      description = "Server not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Server not found")
      )
  )
  @PUT
  ServerRoleRepresentation createRole(@PathParam("id") UUID serverId, CreatedServerRoleRepresentation representation);
}
