package fr.revoicechat.core.web.api;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import fr.revoicechat.core.model.MediaDataStatus;
import fr.revoicechat.core.representation.MediaDataRepresentation;
import fr.revoicechat.core.technicaldata.media.UpdatableMediaDataStatus;
import fr.revoicechat.openapi.api.LoggedApi;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@Path("/media")
@Tag(name = "Media", description = "Manage media files including images, videos, and attachments")
public interface MediaDataController extends LoggedApi {

  @Operation(
      summary = "Get media by ID",
      description = "Retrieve detailed information about a specific media file. Access is restricted to users with appropriate permissions."
  )
  @APIResponse(responseCode = "200", description = "Media retrieved successfully")
  @APIResponse(
      responseCode = "401",
      description = "Insufficient permissions to access this media"
  )
  @APIResponse(
      responseCode = "404",
      description = "Media not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Media not found")
      )
  )
  @GET
  @Path("/{id}")
  MediaDataRepresentation get(@PathParam("id") UUID id);

  @Operation(
      summary = "Update media status",
      description = "Change the status of a media file (e.g., from processing to available). Only the media owner or administrators can update the status."
  )
  @APIResponse(responseCode = "200", description = "Media status updated successfully")
  @APIResponse(
      responseCode = "401",
      description = "Insufficient permissions to update this media"
  )
  @APIResponse(
      responseCode = "404",
      description = "Media not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Media not found")
      )
  )
  @Path("/{id}")
  @PATCH
  MediaDataRepresentation updateMediaByStatus(@PathParam("id") UUID id, UpdatableMediaDataStatus status);

  @Operation(
      summary = "Find media by status",
      description = "Retrieve all media files that match a specific status (e.g., pending, available, deleting)."
  )
  @APIResponse(responseCode = "200", description = "Media list retrieved successfully")
  @GET
  List<MediaDataRepresentation> findMediaByStatus(@QueryParam("status") MediaDataStatus status);

  @Operation(
      summary = "Delete media",
      description = "Mark a media file for deletion by updating its status to DELETING. The actual deletion may occur asynchronously. Only the media owner or administrators can delete media."
  )
  @APIResponse(responseCode = "200", description = "Media marked for deletion successfully")
  @APIResponse(
      responseCode = "401",
      description = "Insufficient permissions to delete this media"
  )
  @APIResponse(
      responseCode = "404",
      description = "Media not found",
      content = @Content(
          mediaType = "text/plain",
          schema = @Schema(implementation = String.class, examples = "Media not found")
      )
  )
  @Path("/{id}")
  @DELETE
  MediaDataRepresentation delete(@PathParam("id") UUID id);
}
