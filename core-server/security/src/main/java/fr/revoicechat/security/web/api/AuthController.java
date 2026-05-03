package fr.revoicechat.security.web.api;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import fr.revoicechat.security.representation.NewPassword;
import fr.revoicechat.security.representation.UserPassword;
import fr.revoicechat.security.representation.UserRecoveryCode;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "Handle user authentication, registration, and session management")
public interface AuthController {

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
  Response login(UserPassword request);

  @Operation(
      summary = "User login",
      description = "Authenticate a user with their username and recovery code. Returns a JWT token that must be included in the Authorization header for subsequent authenticated requests."
  )
  @RequestBody(
      description = "User credentials consisting of username (or display name) and recovery code",
      content = @Content(schema = @Schema(implementation = UserRecoveryCode.class))
  )
  @APIResponse(responseCode = "200", description = "Authentication successful, JWT token returned")
  @APIResponse(responseCode = "401", description = "Authentication failed due to invalid credentials")
  @POST
  @Path("/login/recovery-codes")
  @Produces(MediaType.TEXT_PLAIN)
  Response loginUsingRecoveryCode(UserRecoveryCode request);

  @APIResponse(responseCode = "200", description = "Authentication successful, JWT token returned")
  @APIResponse(responseCode = "401", description = "Authentication failed due to invalid credentials")
  @POST
  @Path("/login/new-password")
  Response updatePasswordAfterRecoveryCode(NewPassword password);

  @RequestBody(
      description = "Regenerate user recovery codes",
      content = @Content(schema = @Schema(implementation = UserPassword.class))
  )
  @APIResponse(responseCode = "200", description = "Authentication successful, recovery codes regenerated")
  @APIResponse(responseCode = "401", description = "Authentication failed due to invalid credentials")
  @POST
  @Path("/recovery-codes")
  @Produces(MediaType.APPLICATION_JSON)
  Response regenerateRecoveryCodes(UserPassword request);

  @Operation(
      summary = "User logout",
      description = "Invalidate the current user session by blacklisting the JWT token. The token will no longer be valid for authentication after this operation."
  )
  @APIResponse(responseCode = "200", description = "User logged out successfully and token blacklisted")
  @APIResponse(responseCode = "204", description = "No active session to log out")
  @GET
  @Path("/logout")
  Response logout();
}
