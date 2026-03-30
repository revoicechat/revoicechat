package fr.revoicechat.risk.web.api;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import fr.revoicechat.risk.representation.ServerRoleRepresentation;
import fr.revoicechat.risk.type.RiskType;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("user")
@Tags(refs = {"Role", "User"})
public interface UserMembershipController {

  @Operation(
      summary = "Get my roles",
      description = "Retrieve all roles assigned to the currently authenticated user across all servers they are a member of."
  )
  @APIResponse(responseCode = "200", description = "User roles retrieved successfully")
  @Path("/me/role")
  @GET
  List<ServerRoleRepresentation> getMyRolesMembership();

  @Operation(
      summary = "Get my permissions in server",
      description = "Retrieve all effective permissions (risk types) for the currently authenticated user within a specific server, calculated from all assigned roles."
  )
  @APIResponse(responseCode = "200", description = "User permissions retrieved successfully")
  @APIResponse(
      responseCode = "403",
      description = "User is not a member of this server"
  )
  @APIResponse(
      responseCode = "404",
      description = "Server not found"
  )
  @Path("/server/{id}/risks")
  @GET
  List<RiskType> getMyRiskType(@PathParam("id") UUID serverId);
}
