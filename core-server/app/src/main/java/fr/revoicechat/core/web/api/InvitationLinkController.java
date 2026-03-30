package fr.revoicechat.core.web.api;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import fr.revoicechat.core.representation.InvitationRepresentation;
import fr.revoicechat.openapi.api.LoggedApi;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@Path("invitation")
@Tag(name = "Invitation", description = "Manage invitations for application and server access")
public interface InvitationLinkController extends LoggedApi {

  @Operation(
      summary = "Generate application invitation",
      description = "Create a new invitation link that allows users to join the application. Requires appropriate permissions."
  )
  @APIResponse(responseCode = "200", description = "Application invitation generated successfully")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to generate application invitations")
  @POST
  @Path("/application")
  InvitationRepresentation generateApplicationInvitation(@QueryParam("category") @DefaultValue("UNIQUE") String category);

  @Tags(refs = { "Server", "Invitation" })
  @Operation(
      summary = "Generate server invitation",
      description = "Create a new invitation link that allows users to join a specific server. Requires appropriate server permissions."
  )
  @APIResponse(responseCode = "200", description = "Server invitation generated successfully")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to generate server invitations")
  @APIResponse(responseCode = "404", description = "Server not found")
  @POST
  @Path("/server/{serverId}")
  InvitationRepresentation generateServerInvitation(@PathParam("serverId") UUID serverId,
                                                    @QueryParam("category") @DefaultValue("UNIQUE") String category);

  @Tags(refs = { "Server", "Invitation" })
  @Operation(
      summary = "Get all server invitations",
      description = "Retrieve all active invitation links for a specific server. Requires appropriate server permissions."
  )
  @APIResponse(responseCode = "200", description = "Server invitations retrieved successfully")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to view server invitations")
  @APIResponse(responseCode = "404", description = "Server not found")
  @GET
  @Path("/server/{serverId}")
  List<InvitationRepresentation> getAllServerInvitations(@PathParam("serverId") UUID serverId);

  @Operation(
      summary = "Get invitation details",
      description = "Retrieve the status and details of a specific invitation by its unique identifier."
  )
  @APIResponse(responseCode = "200", description = "Invitation details retrieved successfully")
  @APIResponse(responseCode = "404", description = "Invitation not found")
  @GET
  @Path("/{id}")
  InvitationRepresentation get(@PathParam("id") UUID id);

  @Operation(
      summary = "Get my invitations",
      description = "Retrieve all invitations created by the currently authenticated user."
  )
  @APIResponse(responseCode = "200", description = "User invitations retrieved successfully")
  @GET
  List<InvitationRepresentation> getAll();

  @Operation(
      summary = "Get all application invitations",
      description = "Retrieve all active invitation links for the application. Requires appropriate permissions."
  )
  @APIResponse(responseCode = "200", description = "Application invitations retrieved successfully")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to view application invitations")
  @GET
  @Path("/application")
  List<InvitationRepresentation> getAllApplicationInvitations();

  @Operation(
      summary = "Revoke invitation",
      description = "Revoke an unused invitation, preventing it from being used. Only the invitation creator or administrators can revoke invitations."
  )
  @APIResponse(responseCode = "200", description = "Invitation revoked successfully")
  @APIResponse(responseCode = "400", description = "Invitation has already been used and cannot be revoked")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to revoke this invitation")
  @APIResponse(responseCode = "404", description = "Invitation not found")
  @DELETE
  @Path("/{id}")
  void revoke(@PathParam("id") UUID id);
}
