package fr.revoicechat.moderation.web.api;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.moderation.representation.SanctionRepresentation;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("user/{userId}/sanctions")
public interface UserSanctionController {

  @GET
  List<SanctionRepresentation> getAppSanctions(@PathParam("userId") UUID userId);

  @GET
  @Path("server/{serverId}")
  List<SanctionRepresentation> getAppSanctions(@PathParam("userId") UUID userId,
                                               @PathParam("serverId") UUID serverId);
}
