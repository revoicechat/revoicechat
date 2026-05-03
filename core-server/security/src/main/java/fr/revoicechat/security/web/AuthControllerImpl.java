package fr.revoicechat.security.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.*;

import java.util.function.Function;

import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.security.representation.NewPassword;
import fr.revoicechat.security.representation.UserPassword;
import fr.revoicechat.security.representation.UserRecoveryCode;
import fr.revoicechat.security.service.AuthenticatedUserService;
import fr.revoicechat.security.service.RecoverCodesService;
import fr.revoicechat.security.service.SecurityTokenService;
import fr.revoicechat.security.utils.PasswordUtils;
import fr.revoicechat.security.web.api.AuthController;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class AuthControllerImpl implements AuthController {

  private final AuthenticatedUserService userService;
  private final SecurityIdentity securityIdentity;
  private final SecurityTokenService securityTokenService;
  private final RecoverCodesService recoverCodesService;

  public AuthControllerImpl(AuthenticatedUserService userService,
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
      return Response.status(Status.UNAUTHORIZED).entity("Invalid credentials").build();
    }
  }

  @Override
  @RolesAllowed(ROLE_RECOVERY)
  public Response updatePasswordAfterRecoveryCode(NewPassword password) {
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

  public Response runWithConnexion(UserPassword request, Function<AuthenticatedUser, Object> generator) {
    var user = userService.findByLogin(request.username());
    if (user != null && PasswordUtils.matches(request.password(), user.getPassword())) {
      return Response.ok(generator.apply(user)).build();
    } else {
      return Response.status(Status.UNAUTHORIZED).entity("Invalid credentials").build();
    }
  }
}
