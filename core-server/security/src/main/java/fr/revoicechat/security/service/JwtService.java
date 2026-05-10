package fr.revoicechat.security.service;

import java.util.Set;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.revoicechat.security.model.AuthenticatedUser;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;

@Default
@Singleton
public class JwtService implements SecurityTokenService {
  private static final Logger LOG = LoggerFactory.getLogger(JwtService.class);

  private static final long HOUR_TIME = 1000L * 3600;
  private static final long DAY_TIME = HOUR_TIME * 24;

  private final JWTParser jwtParser;
  private final TokenBlacklistService tokenBlacklistService;

  @ConfigProperty(name = "mp.jwt.verify.issuer")
  String jwtIssuer;
  @ConfigProperty(name = "revoicechat.jwt.valid-day", defaultValue = "30")
  int jwtValidDay;

  public JwtService(JWTParser jwtParser, TokenBlacklistService tokenBlacklistService) {
    this.jwtParser = jwtParser;
    this.tokenBlacklistService = tokenBlacklistService;
  }

  @Override
  public String generate(final AuthenticatedUser user, Set<String> groups) {
    LOG.info("generate jwt token for user {}", user.getId());
    return Jwt.issuer(jwtIssuer)
              .subject(user.getLogin())
              .preferredUserName(user.getId().toString())
              .groups(groups)
              .expiresAt(System.currentTimeMillis() + DAY_TIME * jwtValidDay)
              .sign();
  }

  @Override
  public String generateTemporaryToken(final AuthenticatedUser user, Set<String> groups) {
    LOG.info("generate temporary jwt token for user {} : {}", user.getId(), groups);
    return Jwt.issuer(jwtIssuer)
              .subject(user.getLogin())
              .preferredUserName(user.getId().toString())
              .groups(groups)
              .expiresAt(System.currentTimeMillis() + HOUR_TIME * 2)
              .sign();
  }

  @Override
  public UUID retrieveUserAsId(final String jwtToken) {
    try {
      JsonWebToken jwt = jwtParser.parse(jwtToken);
      return UUID.fromString(jwt.getName());
    } catch (Exception _) {
      throw new WebApplicationException("Invalid token", 401);
    }
  }

  @Override
  public void blackList(final JsonWebToken jsonWebToken) {
    tokenBlacklistService.blacklistToken(jsonWebToken.getRawToken(), jsonWebToken.getExpirationTime());
  }
}
