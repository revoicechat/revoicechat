package fr.revoicechat.core.web.api;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import fr.revoicechat.core.representation.InvitationRepresentation;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@Path("server")
public interface ServerInvitationController {

  @Tags(refs = { "Server", "Invitation" })
  @Operation(summary = "Generate server invitation", description = "Create a new invitation link for users to join the server. Requires appropriate server permissions.")
  @APIResponse(responseCode = "200", description = "Server invitation generated successfully")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to generate server invitations")
  @APIResponse(responseCode = "404", description = "Server not found")
  @POST
  @Path("/{id}/invitation")
  InvitationRepresentation generateServerInvitation(@PathParam("id") UUID id,
                                                    @QueryParam("category") @DefaultValue("UNIQUE") String category);

  @Tags(refs = { "Server", "Invitation" })
  @Operation(summary = "Get server invitations", description = "Retrieve all active invitation links for a specific server. Requires appropriate server permissions.")
  @APIResponse(responseCode = "200", description = "Server invitations retrieved successfully")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to view server invitations")
  @APIResponse(responseCode = "404", description = "Server not found")
  @GET
  @Path("/{id}/invitation")
  List<InvitationRepresentation> getAllServerInvitations(@PathParam("id") UUID id);

  @Tags(refs = { "Server", "Invitation", "User" })
  @Operation(summary = "Join a public server (no invitation needed)")
  @APIResponse(responseCode = "204", description = "Server successfully joined")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to join this server")
  @APIResponse(responseCode = "404", description = "Server not found")
  @POST
  @Path("/{id}/join")
  void joinPublic(@PathParam("id") UUID serverId);

  @Tags(refs = { "Server", "Invitation", "User" })
  @Operation(summary = "Join a private server via an invitation")
  @APIResponse(responseCode = "204", description = "Server successfully joined")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to join this server")
  @APIResponse(responseCode = "404", description = "Server not found")
  @POST
  @Path("/join/{invitation}")
  void joinPrivate(@PathParam("invitation") UUID invitation);
}
