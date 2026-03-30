package fr.revoicechat.security.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import fr.revoicechat.security.model.AuthenticatedUser;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;

@QuarkusTest
class TestJwtService {

  @Inject SecurityTokenService jwtService;
  @Inject JWTParser jwtParser;

  @Test
  void testUser1() throws ParseException {
    var id = UUID.randomUUID();
    var user = new AuthenticatedUserMock(id, "Rex Woof", "rex_woof", Set.of("ADMIN", "USER"));
    var token = jwtService.generate(user);
    var result = jwtParser.parse(token);
    assertThat(result).isNotNull();
    assertThat(result.getGroups()).containsExactlyInAnyOrder("ADMIN", "USER");
    assertThat(result.getName()).isEqualTo(id.toString());
    assertThat(result.getSubject()).isEqualTo("rex_woof");
    assertThat(jwtService.retrieveUserAsId(token)).isEqualTo(id);
  }

  @Test
  void testUser2() throws ParseException {
    var id = UUID.randomUUID();
    var user = new AuthenticatedUserMock(id, "Nyphew", "nyphew", Set.of("USER", "OTHER"));
    var token = jwtService.generate(user);
    var result = jwtParser.parse(token);
    assertThat(result).isNotNull();
    assertThat(result.getGroups()).containsExactlyInAnyOrder("USER", "OTHER");
    assertThat(result.getName()).isEqualTo(id.toString());
    assertThat(result.getSubject()).isEqualTo("nyphew");
    assertThat(jwtService.retrieveUserAsId(token)).isEqualTo(id);
  }

  private record AuthenticatedUserMock(
      UUID getId,
      String getDisplayName,
      String getLogin,
      Set<String> getRoles
  ) implements AuthenticatedUser {}
}