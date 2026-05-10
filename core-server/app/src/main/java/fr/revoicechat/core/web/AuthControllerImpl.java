package fr.revoicechat.core.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.*;

import java.util.Set;
import java.util.function.Function;

import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.revoicechat.core.representation.NewUserRepresentation;
import fr.revoicechat.core.service.user.UserService;
import fr.revoicechat.core.technicaldata.user.NewUserSignup;
import fr.revoicechat.core.web.api.SignupController;
import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.security.representation.NewPassword;
import fr.revoicechat.security.representation.UserPassword;
import fr.revoicechat.security.representation.UserRecoveryCode;
import fr.revoicechat.security.representation.UserTotpCode;
import fr.revoicechat.security.service.AuthenticatedUserService;
import fr.revoicechat.security.service.RecoverCodesService;
import fr.revoicechat.security.service.SecurityTokenService;
import fr.revoicechat.security.service.TOTPManager;
import fr.revoicechat.security.utils.PasswordUtils;
import fr.revoicechat.security.web.api.AuthController;
import fr.revoicechat.web.mapper.Mapper;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/auth")
public class AuthControllerImpl implements AuthController, SignupController {
  public static final String INVALID_CREDENTIALS = "Invalid credentials";

  private final UserService userService;
  private final AuthenticatedUserService authenticatedUserService;
  private final SecurityIdentity securityIdentity;
  private final SecurityTokenService securityTokenService;
  private final RecoverCodesService recoverCodesService;
  private final TOTPManager totpManager;

  public AuthControllerImpl(UserService userService,
                            AuthenticatedUserService authenticatedUserService,
                            SecurityIdentity securityIdentity,
                            SecurityTokenService securityTokenService,
                            RecoverCodesService recoverCodesService,
                            TOTPManager totpManager) {
    this.userService = userService;
    this.authenticatedUserService = authenticatedUserService;
    this.securityIdentity = securityIdentity;
    this.securityTokenService = securityTokenService;
    this.recoverCodesService = recoverCodesService;
    this.totpManager = totpManager;
  }

  @Override
  @PermitAll
  public NewUserRepresentation signup(NewUserSignup user) {
    return Mapper.map(userService.create(user));
  }

  @Override
  @PermitAll
  public Response login(UserPassword request) {
    return runWithConnexion(request, user -> user.getTotpStatus().active()
           ? Response.ok(securityTokenService.generateTemporaryToken(user, Set.of(ROLE_TOTP_LOGIN)))
                     .header("X-totp-active", "true")
                     .build()
           : Response.ok(securityTokenService.generate(user, user.getRoles())).build());
  }

  @Override
  @RolesAllowed(ROLE_TOTP_LOGIN)
  public Response loginTotp(final UserTotpCode request) {
    return runWithConnexionSingleTime(
        request,
        user -> Response.ok(securityTokenService.generate(user, user.getRoles())).build()
    );
  }

  @Override
  @PermitAll
  public Response loginUsingRecoveryCode(final UserRecoveryCode request) {
    var user = authenticatedUserService.findByLogin(request.username());
    if (user != null && recoverCodesService.consume(user.getId(), request.code())) {
      return user.getTotpStatus().active()
             ? Response.ok(securityTokenService.generateTemporaryToken(user, Set.of(ROLE_TOTP_RECOVERY)))
                       .header("X-totp-active", "true")
                       .build()
             : Response.ok(securityTokenService.generateTemporaryToken(user, Set.of(ROLE_RECOVERY))).build();
    } else {
      return Response.status(Status.UNAUTHORIZED).entity(INVALID_CREDENTIALS).build();
    }
  }

  @Override
  @RolesAllowed(ROLE_TOTP_RECOVERY)
  public Response loginTotpUsingRecoveryCode(final UserTotpCode request) {
    return runWithConnexionSingleTime(
        request,
        user -> Response.ok(securityTokenService.generateTemporaryToken(user, Set.of(ROLE_RECOVERY))).build()
    );
  }

  @Override
  @RolesAllowed(ROLE_RECOVERY)
  public Response updatePasswordAfterRecoveryCode(NewPassword password) {
    JsonWebToken jsonWebToken = (JsonWebToken) securityIdentity.getPrincipal();
    authenticatedUserService.forceSetPassword(password);
    securityTokenService.blackList(jsonWebToken);
    return Response.ok().build();
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public Response regenerateRecoveryCodes(final UserPassword request) {
    return runWithConnexion(request, user -> Response.ok(recoverCodesService.generate(user.getId())).build());
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public Response regenerateTOTPSecret(final UserPassword request) {
    return runWithConnexion(request, user -> Response.ok(totpManager.generate(user.getId()))
                                                     .header("Content-Disposition", "inline; filename=\"totp.png\"")
                                                     .build());
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

  private Response runWithConnexion(UserPassword request, Function<AuthenticatedUser, Response> generator) {
    var user = authenticatedUserService.findByLogin(request.username());
    if (user != null && PasswordUtils.matches(request.password(), user.getPassword())) {
      return generator.apply(user);
    } else {
      return Response.status(Status.UNAUTHORIZED).entity(INVALID_CREDENTIALS).build();
    }
  }

  private Response runWithConnexionSingleTime(UserTotpCode request, Function<AuthenticatedUser, Response> generator) {
    var user = authenticatedUserService.findByLogin(request.username());
    if (totpManager.verify(user, request.code())) {
      if (securityIdentity.getPrincipal() instanceof JsonWebToken jsonWebToken) {
        securityTokenService.blackList(jsonWebToken);
      }
      return generator.apply(user);
    } else {
      return Response.status(Status.UNAUTHORIZED).entity(INVALID_CREDENTIALS).build();
    }
  }
}
