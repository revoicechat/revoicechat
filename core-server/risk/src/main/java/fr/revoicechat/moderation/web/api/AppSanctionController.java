package fr.revoicechat.moderation.web.api;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import fr.revoicechat.moderation.representation.NewSanction;
import fr.revoicechat.moderation.representation.SanctionFilterParams;
import fr.revoicechat.moderation.representation.SanctionRepresentation;
import fr.revoicechat.moderation.representation.SanctionRevocationRequestRepresentation;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("sanctions")
@Tag(name = "Sanctions", description = "Manage app sanctions")
public interface AppSanctionController {

  @Operation(summary = "Get all sanctions with potential filter")
  @APIResponse(responseCode = "200", description = "Sanctions retrieved successfully")
  @GET
  List<SanctionRepresentation> getSanctions(@BeanParam SanctionFilterParams params);

  @Operation(summary = "Get a specific sanction by ID")
  @APIResponse(responseCode = "200", description = "Sanction retrieved successfully")
  @GET
  @Path("/{id}")
  SanctionRepresentation getSanction(@PathParam("id") UUID id);

  @Operation(summary = "Issue an app-level sanction (ban, voice/text timeout)")
  @APIResponse(responseCode = "200", description = "Sanction successfully created")
  @POST
  SanctionRepresentation issueAppLevelSanction(NewSanction newSanction);

  @Operation(summary = "Revoke an active app-level sanction")
  @APIResponse(responseCode = "204", description = "Sanction successfully revoked")
  @DELETE
  @Path("/{id}")
  void revokeAppLevelSanction(@PathParam("id") UUID id);

  @Operation(summary = "Ask to revoke a sanction")
  @APIResponse(responseCode = "204", description = "Sanction successfully asked")
  @PATCH
  @Path("{id}")
  SanctionRevocationRequestRepresentation askToRevokeSanction(@PathParam("id") UUID sanctionId, String pledgeMessage);

  @Operation(summary = "Reject an active app-level sanction")
  @APIResponse(responseCode = "204", description = "Sanction successfully reject")
  @PATCH
  @Path("{id}")
  void rejectRevokeSanctionRequest(@PathParam("id") UUID sanctionId);

  @GET
  @Path("revocation-requests")
  List<SanctionRevocationRequestRepresentation> fetchActiveRevocationRequest();
}
