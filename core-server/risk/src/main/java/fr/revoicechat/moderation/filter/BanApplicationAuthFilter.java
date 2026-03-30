package fr.revoicechat.moderation.filter;

import fr.revoicechat.moderation.service.SanctionService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHORIZATION + 1)
@SuppressWarnings("java:S6813") // inject annotation must be used in request filter
public class BanApplicationAuthFilter implements ContainerRequestFilter {

  @Inject SecurityIdentity identity;
  @Inject SanctionService sanctionService;

  @Override
  public void filter(ContainerRequestContext requestContext) {
    if (identity.isAnonymous() || requestContext.getUriInfo().getPath().startsWith("/sanction")) {
      return;
    }
    if (sanctionService.isAppBanned()) {
      requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                                       .entity(new BanResponse("You were banned!"))
                                       .type(MediaType.APPLICATION_JSON)
                                       .build());
    }
  }

  record BanResponse(String message) {}
}