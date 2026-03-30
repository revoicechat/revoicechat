package fr.revoicechat.core.web;

import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.revoicechat.core.representation.UserRepresentation;
import fr.revoicechat.core.service.user.UserService;
import fr.revoicechat.core.technicaldata.login.UserPassword;
import fr.revoicechat.core.technicaldata.user.NewUserSignup;
import fr.revoicechat.core.web.api.AuthController;
import fr.revoicechat.security.service.SecurityTokenService;
import fr.revoicechat.security.utils.PasswordUtils;
import fr.revoicechat.web.mapper.Mapper;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@PermitAll
public class AuthControllerImpl implements AuthController {

  private final UserService userService;
  private final SecurityIdentity securityIdentity;
  private final SecurityTokenService securityTokenService;

  public AuthControllerImpl(UserService userService,
                            SecurityIdentity securityIdentity,
                            SecurityTokenService securityTokenService) {
    this.userService = userService;
    this.securityIdentity = securityIdentity;
    this.securityTokenService = securityTokenService;
  }

  @Override
  public UserRepresentation signup(NewUserSignup user) {
    return Mapper.map(userService.create(user));
  }

  @Override
  public Response login(UserPassword request) {
    var user = userService.findByLogin(request.username());
    if (user != null && PasswordUtils.matches(request.password(), user.getPassword())) {
      return Response.ok(securityTokenService.generate(user)).build();
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
    }
  }

  @Override
  public Response logout() {
    if (securityIdentity.getPrincipal() instanceof JsonWebToken jsonWebToken) {
      securityTokenService.blackList(jsonWebToken);
      return Response.ok().build();
    } else {
      return Response.status(Status.NO_CONTENT).build();
    }
  }
}
