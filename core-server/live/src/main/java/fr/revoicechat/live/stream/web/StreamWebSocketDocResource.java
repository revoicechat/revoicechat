package fr.revoicechat.live.stream.web;

import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.Operation;

import fr.revoicechat.live.common.web.WebSocketApi;
import fr.revoicechat.live.stream.socket.StreamWebSocket;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Here for documentation purpose.
 *
 * @see StreamWebSocket
 */
@Path("/ws/stream")
@Produces(MediaType.APPLICATION_JSON)
public class StreamWebSocketDocResource implements WebSocketApi {

  @GET
  @Operation(summary = "WebSocket endpoint for voice",
      description = """
          Connect via `ws://*url*/api/voice/{roomId}?token={jwtToken}`.
            - Text messages: JSON control
            - Binary messages: audio or video chunks
          @param userId: id of the user that is streaming something. it is mostly a webcam or a screen share.
          @param name:   name of the stream. it's a technical param that allow a user to browse multiple stream at the same time.
                         With that a user can stream his webcam, at the same time of all his screens, and another user can watch only a specific stream""")
  public Response wsStreamInfo() {
    return Response.ok(Map.of("url", "ws://*url*/api/stream/{userId}/{name}")).build();
  }
}
