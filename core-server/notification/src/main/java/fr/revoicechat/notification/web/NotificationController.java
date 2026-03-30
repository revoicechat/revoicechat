package fr.revoicechat.notification.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_USER;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.SseElementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.revoicechat.notification.NotificationRegistrableHolder;
import fr.revoicechat.notification.service.NotificationRegistry;
import fr.revoicechat.openapi.api.LoggedApi;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.SseEventSink;

@PermitAll
@Path("/sse")
@Tag(name = "Notification", description = "Manage real-time notification using Server-Sent Events (SSE)")
public class NotificationController implements LoggedApi {
  private static final Logger LOG = LoggerFactory.getLogger(NotificationController.class);

  private final NotificationRegistry notificationRegistry;
  private final NotificationRegistrableHolder holder;

  public NotificationController(NotificationRegistry notificationRegistry, NotificationRegistrableHolder holder) {
    this.notificationRegistry = notificationRegistry;
    this.holder = holder;
  }

  @Operation(summary = "Register a user in the notification center",
      description = """
          Register a user in the notification center.
          A user can be registered multiple times.
          SSE stream emitting various notification payloads.""")
  @APIResponse(
      responseCode = "200",
      description = "SSE stream successfully opened",
      content = @Content(mediaType = MediaType.APPLICATION_JSON)
  )
  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @SseElementType(MediaType.APPLICATION_JSON)
  @RolesAllowed(ROLE_USER)
  public void generateSseEmitter(@Context SseEventSink sink) {
    var user = holder.get();
    notificationRegistry.register(user, sink);
    LOG.info("sse connection for user {}", user.getId());
  }
}
