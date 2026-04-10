package fr.revoicechat.core.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.*;

import java.util.function.Function;

import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.revoicechat.core.model.User;
import fr.revoicechat.core.representation.UserRepresentation;
import fr.revoicechat.core.service.user.UserService;
import fr.revoicechat.core.technicaldata.login.UserPassword;
import fr.revoicechat.core.technicaldata.login.UserRecoveryCode;
import fr.revoicechat.core.technicaldata.user.NewUserSignup;
import fr.revoicechat.core.web.api.AuthController;
import fr.revoicechat.security.service.RecoverCodesService;
import fr.revoicechat.security.service.SecurityTokenService;
import fr.revoicechat.security.utils.PasswordUtils;
import fr.revoicechat.web.mapper.Mapper;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class AuthControllerImpl implements AuthController {

  private final UserService userService;
  private final SecurityIdentity securityIdentity;
  private final SecurityTokenService securityTokenService;
  private final RecoverCodesService recoverCodesService;

  public AuthControllerImpl(UserService userService,
                            SecurityIdentity securityIdentity,
                            SecurityTokenService securityTokenService,
                            RecoverCodesService recoverCodesService) {
    this.userService = userService;
    this.securityIdentity = securityIdentity;
    this.securityTokenService = securityTokenService;
    this.recoverCodesService = recoverCodesService;
  }

  @Override
  @PermitAll
  public UserRepresentation signup(NewUserSignup user) {
    return Mapper.map(userService.create(user));
  }

  @Override
  @PermitAll
  public Response login(UserPassword request) {
    return runWithConnexion(request, securityTokenService::generate);
  }

  @Override
  @PermitAll
  public Response loginUsingRecoveryCode(final UserRecoveryCode request) {
    var user = userService.findByLogin(request.username());
    if (user != null && recoverCodesService.consume(user, request.code())) {
      return Response.ok(securityTokenService.generateAfterRecoveryCode(user)).build();
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
    }
  }

  @Override
  @RolesAllowed(ROLE_RECOVERY)
  public Response updatePasswordAfterRecoveryCode(String password) {
    if (securityIdentity.getPrincipal() instanceof JsonWebToken jsonWebToken) {
      userService.forceSetPassword(password);
      securityTokenService.blackList(jsonWebToken);
      return Response.ok().build();
    } else {
      return Response.status(Status.NO_CONTENT).build();
    }
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public Response regenerateRecoveryCodes(final UserPassword request) {
    return runWithConnexion(request, recoverCodesService::generate);
  }

  @Override
  @PermitAll
  public Response logout() {
    if (securityIdentity.getPrincipal() instanceof JsonWebToken jsonWebToken) {
      securityTokenService.blackList(jsonWebToken);
      return Response.ok().build();
    } else {
      return Response.status(Status.NO_CONTENT).build();
    }
  }

  public Response runWithConnexion(UserPassword request, Function<User, Object> generator) {
    var user = userService.findByLogin(request.username());
    if (user != null && PasswordUtils.matches(request.password(), user.getPassword())) {
      return Response.ok(generator.apply(user)).build();
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
    }
  }
}
