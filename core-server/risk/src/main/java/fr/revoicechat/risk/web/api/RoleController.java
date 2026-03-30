package fr.revoicechat.risk.web.api;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import fr.revoicechat.risk.representation.CreatedServerRoleRepresentation;
import fr.revoicechat.risk.representation.RiskUpdateRepresentation;
import fr.revoicechat.risk.representation.ServerRoleRepresentation;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("role/{id}")
@Tag(name = "Role", description = "Manage individual roles and their permissions")
public interface RoleController {

  @Operation(
      summary = "Get role by ID",
      description = "Retrieve detailed information about a specific role including its permissions and assigned users."
  )
  @APIResponse(responseCode = "200", description = "Role retrieved successfully")
  @APIResponse(
      responseCode = "404",
      description = "Role not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Role not found")
      )
  )
  @GET
  ServerRoleRepresentation getRole(@PathParam("id") UUID roleId);

  @Operation(
      summary = "Update role",
      description = "Update the properties of an existing role such as name, color, or position. Requires server administrative permissions."
  )
  @APIResponse(responseCode = "200", description = "Role updated successfully")
  @APIResponse(responseCode = "400", description = "Invalid role data provided")
  @APIResponse(
      responseCode = "403",
      description = "Insufficient permissions to update this role"
  )
  @APIResponse(
      responseCode = "404",
      description = "Role not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Role not found")
      )
  )
  @PATCH
  ServerRoleRepresentation updateRole(@PathParam("id") UUID roleId, CreatedServerRoleRepresentation representation);

  @Operation(
      summary = "Assign role to users",
      description = "Add a specific role to multiple users. Users will inherit all permissions associated with this role. Requires server administrative permissions."
  )
  @APIResponse(responseCode = "200", description = "Role assigned to users successfully")
  @APIResponse(responseCode = "400", description = "Invalid user IDs provided")
  @APIResponse(
      responseCode = "403",
      description = "Insufficient permissions to assign roles"
  )
  @APIResponse(
      responseCode = "404",
      description = "Role not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Role not found")
      )
  )
  @Path("user")
  @PUT
  void addUserToRole(@PathParam("id") UUID roleId, List<UUID> users);

  @Operation(
      summary = "Remove role from users",
      description = "Remove a specific role from multiple users. Users will lose all permissions associated with this role. Requires server administrative permissions."
  )
  @APIResponse(responseCode = "200", description = "Role removed from users successfully")
  @APIResponse(responseCode = "400", description = "Invalid user IDs provided")
  @APIResponse(
      responseCode = "403",
      description = "Insufficient permissions to remove roles"
  )
  @APIResponse(
      responseCode = "404",
      description = "Role not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Role not found")
      )
  )
  @Path("user")
  @DELETE
  void removeUserToRole(@PathParam("id") UUID roleId, List<UUID> users);

  @Operation(
      summary = "Update role permission",
      description = "Add or update a specific permission (risk) for a role. The mode parameter determines whether the permission is allowed, denied, or neutral. Requires server administrative permissions."
  )
  @APIResponse(responseCode = "200", description = "Permission updated successfully")
  @APIResponse(responseCode = "400", description = "Invalid risk type or mode provided")
  @APIResponse(
      responseCode = "403",
      description = "Insufficient permissions to modify role permissions"
  )
  @APIResponse(
      responseCode = "404",
      description = "Role not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Role not found")
      )
  )
  @Path("risk/{type}")
  @PATCH
  void patchOrAddRisk(@PathParam("id") UUID roleId,
                      @PathParam("type") String type, RiskUpdateRepresentation updateRepresentation);
}
