package fr.revoicechat.core.web.api;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import fr.revoicechat.openapi.api.LoggedApi;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/profile")
@Tag(name = "Profile Picture", description = "Manage Profil picture of a user or a server")
public interface ProfilPictureController extends LoggedApi {

  @Operation(summary = "Emmit a sse message to trigger profil picture modification")
  @APIResponse(responseCode = "200", description = "Profil picture updated successfully")
  @APIResponse(
      responseCode = "401",
      description = "Insufficient permissions to update profil picture"
  )
  @APIResponse(
      responseCode = "404",
      description = "User or Server not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Profil picture holder not found")
      )
  )
  @Path("/{id}")
  @PATCH
  void updateProfilPicture(@PathParam("id") UUID id);
}
