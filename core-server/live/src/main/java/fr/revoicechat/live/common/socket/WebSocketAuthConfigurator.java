package fr.revoicechat.live.common.socket;

import java.util.List;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketAuthConfigurator extends ServerEndpointConfig.Configurator {
  private static final Logger LOG = LoggerFactory.getLogger(WebSocketAuthConfigurator.class);

  @Override
  public String getNegotiatedSubprotocol(List<String> supported, List<String> requested) {
    LOG.debug("Requested subprotocols: {}", requested);
    for (String protocol : requested) {
      if (protocol.startsWith("Bearer.")) {
        LOG.debug("Negotiated subprotocol: {}", protocol);
        return protocol;
      }
    }
    return null;
  }

  @Override
  public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
    config.getUserProperties().put("auth-token", null);
    List<String> subprotocols = request.getHeaders().get("Sec-WebSocket-Protocol");
    if (subprotocols != null && !subprotocols.isEmpty()) {
      String subprotocol = subprotocols.getFirst();
      if (subprotocol.startsWith("Bearer.")) {
        config.getUserProperties().put("auth-token", subprotocol.substring(7));
      }
    }
    super.modifyHandshake(config, request, response);
  }
}