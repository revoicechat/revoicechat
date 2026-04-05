package fr.revoicechat.moderation.web.api;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import fr.revoicechat.moderation.representation.NewSanction;
import fr.revoicechat.moderation.representation.SanctionRepresentation;
import fr.revoicechat.moderation.representation.SanctionRevocationRequestRepresentation;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("servers/{serverId}/sanctions")
@Tag(name = "Sanctions")
@Tag(name = "Server")
public interface ServerSanctionController {

  @Operation(summary = "Get a server-level sanction by ID")
  @APIResponse(responseCode = "200", description = "Sanction retrieved successfully")
  @GET
  @Path("/{id}")
  SanctionRepresentation getSanction(@PathParam("serverId") UUID serverId,
                                     @PathParam("id") UUID id);

  @Operation(summary = "Issue an app-level sanction (ban, voice/text timeout)")
  @APIResponse(responseCode = "200", description = "Sanction successfully created")
  @POST
  SanctionRepresentation issueServerLevelSanction(@PathParam("serverId") UUID serverId,
                                               NewSanction newSanction);

  @Operation(summary = "Revoke an active server-level sanction")
  @APIResponse(responseCode = "204", description = "Sanction successfully revoked")
  @DELETE
  @Path("/{id}")
  void revokeServerLevelSanction(@PathParam("serverId") UUID serverId, @PathParam("id") UUID id);

  @Operation(summary = "Reject an active server-level sanction")
  @APIResponse(responseCode = "204", description = "Sanction successfully revoked")
  @PATCH
  @Path("{id}")
  void rejectRevokeSanctionRequest(@PathParam("serverId") UUID serverId, @PathParam("id") UUID id);

  @GET
  @Path("revocation-requests")
  List<SanctionRevocationRequestRepresentation> fetchActiveRevocationRequest(@PathParam("serverId") UUID serverId);
}
