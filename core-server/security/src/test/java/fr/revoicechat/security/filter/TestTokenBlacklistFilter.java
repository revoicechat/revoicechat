package fr.revoicechat.security.filter;

import java.io.InputStream;
import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Test;

import fr.revoicechat.security.service.TokenBlacklistService;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.auth.cdi.NullJsonWebToken;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

@QuarkusTest
class TestTokenBlacklistFilter {

  @Inject TokenBlacklistService blacklistService;
  @Inject TokenBlacklistFilter tokenBlacklistFilter;

  @Test
  void testWithNoToken() {
    // Given
    var ctx = new ContainerRequestContextMock(null);
    // When
    tokenBlacklistFilter.filter(ctx);
    // Then
    Assertions.assertThat(ctx.response).isNull();
  }

  @Test
  void testWithNullToken() {
    // Given
    JsonWebToken jsonWebToken = new NullJsonWebToken();
    var ctx = new ContainerRequestContextMock(jsonWebToken);
    // When
    tokenBlacklistFilter.filter(ctx);
    // Then
    Assertions.assertThat(ctx.response).isNull();
  }

  @Test
  void testWithValidToken() {
    // Given
    JsonWebToken jsonWebToken = new JsonWebTokenMock("testWithValidToken", System.currentTimeMillis() + 999999);
    var ctx = new ContainerRequestContextMock(jsonWebToken);
    // When
    tokenBlacklistFilter.filter(ctx);
    // Then
    Assertions.assertThat(ctx.response).isNull();
  }

  @Test
  void testWithExpiredToken() {
    // Given
    JsonWebToken jsonWebToken = new JsonWebTokenMock("testWithExpiredToken", System.currentTimeMillis() - 999999);
    var ctx = new ContainerRequestContextMock(jsonWebToken);
    // When
    tokenBlacklistFilter.filter(ctx);
    // Then
    Assertions.assertThat(ctx.response).isNotNull();
  }

  @Test
  void testWithBlacklistedToken() {
    // Given
    JsonWebToken jsonWebToken = new JsonWebTokenMock("testWithBlacklistedToken", System.currentTimeMillis() + 999999);
    var ctx = new ContainerRequestContextMock(jsonWebToken);
    blacklistService.blacklistToken("testWithBlacklistedToken", System.currentTimeMillis() + 999999);
    // When
    tokenBlacklistFilter.filter(ctx);
    // Then
    Assertions.assertThat(ctx.response).isNotNull();
  }

  private record JsonWebTokenMock(String rawToken, long expirationTime) implements JsonWebToken {
    @Override public String getName() {return "";}
    @Override public Set<String> getClaimNames() {return Set.of();}
    @Override public <T> T getClaim(final String claimName) {return null;}

    @Override
    public String getRawToken() {
      return rawToken;
    }

    @Override
    public long getExpirationTime() {
      return expirationTime;
    }
  }

  private static final class ContainerRequestContextMock implements ContainerRequestContext {
    private final Principal principal;
    private Response response = null;

    private ContainerRequestContextMock(Principal principal) {this.principal = principal;}

    @Override public Object getProperty(final String name) {return null;}
    @Override public Collection<String> getPropertyNames() {return List.of();}
    @Override public void setProperty(final String name, final Object object) {/* */}
    @Override public void removeProperty(final String name) {/* */}
    @Override public UriInfo getUriInfo() {return null;}
    @Override public void setRequestUri(final URI requestUri) {/* */}
    @Override public void setRequestUri(final URI baseUri, final URI requestUri) {/* */}
    @Override public Request getRequest() {return null;}
    @Override public String getMethod() {return "";}
    @Override public void setMethod(final String method) {/* */}
    @Override public MultivaluedMap<String, String> getHeaders() {return null;}
    @Override public String getHeaderString(final String name) {return "";}
    @Override public Date getDate() {return null;}
    @Override public Locale getLanguage() {return null;}
    @Override public int getLength() {return 0;}
    @Override public MediaType getMediaType() {return null;}
    @Override public List<MediaType> getAcceptableMediaTypes() {return List.of();}
    @Override public List<Locale> getAcceptableLanguages() {return List.of();}
    @Override public Map<String, Cookie> getCookies() {return Map.of();}
    @Override public boolean hasEntity() {return false;}
    @Override public InputStream getEntityStream() {return null;}
    @Override public void setEntityStream(final InputStream input) {/* */}
    @Override public void setSecurityContext(final SecurityContext context) {/* */}

    @Override
    public void abortWith(final Response response) {
      this.response = response;
    }

    @Override
    public SecurityContext getSecurityContext() {
      return new SecurityContext() {
        @Override public boolean isUserInRole(final String role) {return false;}
        @Override public boolean isSecure() {return false;}
        @Override public String getAuthenticationScheme() {return "";}

        @Override
        public Principal getUserPrincipal() {
          return principal;
        }
      };
    }
  }
}