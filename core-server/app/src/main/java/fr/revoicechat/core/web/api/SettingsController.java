package fr.revoicechat.core.web.api;

import java.util.Map;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import fr.revoicechat.openapi.api.LoggedApi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/settings")
@Tag(name = "Settings", description = "Manage application and user settings")
public interface SettingsController extends LoggedApi  {

  @Operation(
      summary = "Get general settings",
      description = "Retrieve global application settings and configuration values."
  )
  @APIResponse(responseCode = "200", description = "General settings retrieved successfully")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  Map<String, Object> genealSetings();

  @Tag(name = "User")
  @Operation(
      summary = "Get my settings",
      description = "Retrieve the personal settings and preferences for the currently authenticated user."
  )
  @APIResponse(responseCode = "200", description = "User settings retrieved successfully")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  @Path("/me")
  String me();

  @Tag(name = "User")
  @Operation(
      summary = "Get user settings",
      description = "Retrieve the settings for a specific user. Administrators can view settings for any user, while regular users can only view their own settings."
  )
  @APIResponse(responseCode = "200", description = "User settings retrieved successfully")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to view this user's settings")
  @APIResponse(responseCode = "404", description = "User not found")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  @Path("/user/{id}")
  String ofUser(@PathParam("id") UUID id);

  @Tag(name = "User")
  @Operation(
      summary = "Update my settings",
      description = "Update personal settings and preferences for the currently authenticated user."
  )
  @APIResponse(responseCode = "200", description = "User settings updated successfully")
  @APIResponse(responseCode = "400", description = "Invalid settings data provided")
  @Produces(MediaType.APPLICATION_JSON)
  @PATCH
  @Path("/me")
  String me(String settings);
}
