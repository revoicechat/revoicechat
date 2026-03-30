package fr.revoicechat.stub.voice.socket;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.CloseReason;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

record WebSocket(Session session,
                 BlockingQueue<byte[]> byteMessages,
                 BlockingQueue<String> messages,
                 AtomicReference<CloseReason> closeReason) implements AutoCloseable {

  public static WebSocket of(UUID roomId, String token) throws DeploymentException, IOException {
    return of("ws://localhost:8081/api/voice/" + roomId, List.of("Bearer." + token));
  }

  public static WebSocket of(String url) throws DeploymentException, IOException {
    return of(url, Collections.emptyList());
  }

  public static WebSocket of(String url, List<String> subprotocols) throws DeploymentException, IOException {
    BlockingQueue<String> messages = new ArrayBlockingQueue<>(1);
    BlockingQueue<byte[]> byteMessages = new ArrayBlockingQueue<>(1);
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    var endpoint = new Endpoint() {
      final AtomicReference<CloseReason> closeReason = new AtomicReference<>();

      @Override
      public void onOpen(Session session, EndpointConfig config) {
        session.addMessageHandler(String.class, messages::offer);
        session.addMessageHandler(byte[].class, byteMessages::offer);
      }

      @Override
      public void onClose(final Session session, final CloseReason closeReason) {
        this.closeReason.set(closeReason);
        super.onClose(session, closeReason);
      }
    };
    Session session = container.connectToServer(endpoint,
                                                ClientEndpointConfig.Builder.create().preferredSubprotocols(subprotocols).build(),
                                                URI.create(url));
    return new WebSocket(session, byteMessages, messages, endpoint.closeReason);
  }

  void send(String message) {
    session.getAsyncRemote().sendText(message);
  }

  void send(byte[] message) {
    session.getAsyncRemote().sendBinary(ByteBuffer.wrap(message));
  }

  String getMessage() throws InterruptedException {
    return messages.poll(1, TimeUnit.SECONDS);
  }

  byte[] getByteMessage() throws InterruptedException {
    return byteMessages.poll(1, TimeUnit.SECONDS);
  }

  @Override
  public void close() throws IOException {session.close();}
}