package fr.revoicechat.core.web.api;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import fr.revoicechat.core.representation.NewUserRepresentation;
import fr.revoicechat.core.technicaldata.user.NewUserSignup;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
  NewUserRepresentation signup(NewUserSignup user);
}
