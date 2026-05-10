package fr.revoicechat.security.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.security.model.UserType;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.inject.Inject;

@QuarkusTest
class TestJwtService {

  @Inject SecurityTokenService jwtService;
  @Inject JWTParser jwtParser;

  @Test
  void testUser1() throws ParseException {
    var user = newAuthenticatedUser("Rex Woof", "rex_woof", UserType.ADMIN);
    var token = jwtService.generate(user, UserType.ADMIN.getRoles());
    var result = jwtParser.parse(token);
    assertThat(result).isNotNull();
    assertThat(result.getGroups()).containsExactlyInAnyOrder("ADMIN", "USER");
    assertThat(result.getName()).isEqualTo(user.getId().toString());
    assertThat(result.getSubject()).isEqualTo("rex_woof");
    assertThat(jwtService.retrieveUserAsId(token)).isEqualTo(user.getId());
  }

  @Test
  void testUser2() throws ParseException {
    var user = newAuthenticatedUser("Nyphew", "nyphew", UserType.USER);
    var token = jwtService.generate(user, UserType.USER.getRoles());
    var result = jwtParser.parse(token);
    assertThat(result).isNotNull();
    assertThat(result.getGroups()).containsExactlyInAnyOrder("USER");
    assertThat(result.getName()).isEqualTo(user.getId().toString());
    assertThat(result.getSubject()).isEqualTo("nyphew");
    assertThat(jwtService.retrieveUserAsId(token)).isEqualTo(user.getId());
  }

  private AuthenticatedUser newAuthenticatedUser(String displayName, String login, UserType userType) {
    var user = new AuthenticatedUser();
    user.setId(UUID.randomUUID());
    user.setDisplayName(displayName);
    user.setLogin(login);
    user.setType(userType);
    return user;
  }
}