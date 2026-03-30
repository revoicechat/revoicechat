package fr.revoicechat.core.web.api;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import fr.revoicechat.core.representation.UserRepresentation;
import fr.revoicechat.core.technicaldata.login.UserPassword;
import fr.revoicechat.core.technicaldata.user.NewUserSignup;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "Handle user authentication, registration, and session management")
public interface AuthController {

  @Operation(
      summary = "Register new user",
      description = "Create a new user account with the provided registration details. Upon successful registration, the user can log in using their credentials."
  )
  @RequestBody(
      description = "User registration information including username, password, and optional profile details",
      content = @Content(schema = @Schema(implementation = NewUserSignup.class))
  )
  @APIResponse(responseCode = "200", description = "User account created successfully")
  @APIResponse(responseCode = "400", description = "Invalid registration data or username already exists")
  @APIResponse(responseCode = "409", description = "Username or email already in use")
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/signup")
  UserRepresentation signup(NewUserSignup user);

  @Operation(
      summary = "User login",
      description = "Authenticate a user with their username and password. Returns a JWT token that must be included in the Authorization header for subsequent authenticated requests."
  )
  @RequestBody(
      description = "User credentials consisting of username (or display name) and password",
      content = @Content(schema = @Schema(implementation = UserPassword.class))
  )
  @APIResponse(responseCode = "200", description = "Authentication successful, JWT token returned")
  @APIResponse(responseCode = "401", description = "Authentication failed due to invalid credentials")
  @POST
  @Path("/login")
  @Produces(MediaType.TEXT_PLAIN)
  @PermitAll
  Response login(UserPassword request);

  @Operation(
      summary = "User logout",
      description = "Invalidate the current user session by blacklisting the JWT token. The token will no longer be valid for authentication after this operation."
  )
  @APIResponse(responseCode = "200", description = "User logged out successfully and token blacklisted")
  @APIResponse(responseCode = "204", description = "No active session to log out")
  @GET
  @Path("/logout")
  @Produces(MediaType.TEXT_PLAIN)
  @PermitAll
  Response logout();
}
