package fr.revoicechat.core.web.api;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import fr.revoicechat.core.representation.MessageRepresentation;
import fr.revoicechat.core.representation.RoomRepresentation;
import fr.revoicechat.core.representation.UserRepresentation;
import fr.revoicechat.core.technicaldata.message.NewMessage;
import fr.revoicechat.core.technicaldata.user.AdminUpdatableUserData;
import fr.revoicechat.core.technicaldata.user.UpdatableUserData;
import fr.revoicechat.openapi.api.LoggedApi;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "User", description = "Manage user profiles and information")
public interface UserController extends LoggedApi {

  @Operation(
      summary = "Get my profile",
      description = "Retrieve the complete profile information for the currently authenticated user."
  )
  @APIResponse(responseCode = "200", description = "User profile retrieved successfully")
  @GET
  @Path("/me")
  UserRepresentation me();

  @Operation(summary = "Update my profile", description = "Update personal profile information for the currently authenticated user. Only non-null fields will be updated.")
  @APIResponse(responseCode = "200", description = "User profile updated successfully")
  @APIResponse(responseCode = "400", description = "Invalid user data provided")
  @PATCH
  @Path("/me")
  UserRepresentation updateMe(UpdatableUserData userData);

  @Operation(summary = "Get user by ID", description = "Retrieve the profile information for a specific user. Some fields may be hidden based on privacy settings.")
  @APIResponse(responseCode = "200", description = "User profile retrieved successfully")
  @APIResponse(responseCode = "404", description = "User not found")
  @GET
  @Path("/{id}")
  UserRepresentation get(@PathParam("id") UUID id);

  @GET
  @Path("/{id}/private-message")
  RoomRepresentation getPrivateMessage(@PathParam("id") UUID id);

  @POST
  @Path("/{id}/private-message")
  MessageRepresentation sendPrivateMessage(@PathParam("id") UUID id, NewMessage representation);

  @Operation(summary = "Update user (admin)", description = "Update specific properties of any user profile. This endpoint is restricted to administrators and allows updating display name and user type (USER/BOT/ADMIN).")
  @APIResponse(responseCode = "200", description = "User profile updated successfully")
  @APIResponse(responseCode = "400", description = "Invalid user data provided")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to update this user")
  @APIResponse(responseCode = "404", description = "User not found")
  @PATCH
  @Path("/{id}")
  UserRepresentation updateAsAdmin(@PathParam("id") UUID id, AdminUpdatableUserData userData);

  @Operation(summary = "Get all users", description = "Retrieve a list of all users in the application. This endpoint may be restricted based on user permissions.")
  @APIResponse(responseCode = "200", description = "User list retrieved successfully")
  @APIResponse(responseCode = "403", description = "Insufficient permissions to view all users")
  @GET
  List<UserRepresentation> fetchAll();
}
