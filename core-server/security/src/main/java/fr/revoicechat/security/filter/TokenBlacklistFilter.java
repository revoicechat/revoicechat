package fr.revoicechat.security.filter;

import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.revoicechat.security.service.TokenBlacklistService;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class TokenBlacklistFilter implements ContainerRequestFilter {

    @SuppressWarnings("java:S6813") // inject annotation must be used in request filter
    @Inject
    TokenBlacklistService blacklistService;

    @Override
    public void filter(ContainerRequestContext ctx) {
        if (ctx.getSecurityContext().getUserPrincipal() instanceof JsonWebToken jsonWebToken) {
            String token = jsonWebToken.getRawToken();
            if (token != null && isInvalid(jsonWebToken)) {
                ctx.abortWith(Response.status(401).build());
            }
        }
    }

    private boolean isInvalid(final JsonWebToken jsonWebToken) {
        return blacklistService.isBlacklisted(jsonWebToken.getRawToken())
               || isExpired(jsonWebToken.getExpirationTime());
    }

    private boolean isExpired(final long expirationTime) {
    return System.currentTimeMillis() > expirationTime;
  }
}