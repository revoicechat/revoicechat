package fr.revoicechat.core.web.api;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import fr.revoicechat.core.model.server.ServerStructure;
import fr.revoicechat.openapi.api.LoggedApi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("server/{id}/structure")
public interface ServerStructureController extends LoggedApi {

  @Tags(refs = { "Server" })
  @Operation(summary = "Get server structure", description = "Retrieve the organizational structure of a server including categories, channels, and their hierarchy.")
  @APIResponse(responseCode = "200", description = "Server structure retrieved successfully")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to access this server")
  @APIResponse(responseCode = "404", description = "Server not found")
  @GET
  ServerStructure getStructure(@PathParam("id") final UUID id);

  @Operation(summary = "Update server structure", description = "Update the organizational structure of a server including reordering categories and channels. Requires server administrative permissions.")
  @APIResponse(responseCode = "200", description = "Server structure updated successfully")
  @APIResponse(responseCode = "400", description = "Invalid structure data provided")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to update server structure")
  @APIResponse(responseCode = "404", description = "Server not found")
  @PATCH
  ServerStructure patchStructure(@PathParam("id") final UUID id, ServerStructure structure);
}
