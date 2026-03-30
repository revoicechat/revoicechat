package fr.revoicechat.security.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_USER;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/tests/secured-endpoint")
public class SecuredEndpointTestController {

  @GET
  @RolesAllowed(ROLE_USER)
  public DevOnlyData securedEndpoint() {
    return new DevOnlyData("secured-endpoint");
  }

  public record DevOnlyData(String data) {}
}
