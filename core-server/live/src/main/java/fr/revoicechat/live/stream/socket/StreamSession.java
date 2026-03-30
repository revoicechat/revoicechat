package fr.revoicechat.live.stream.socket;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import fr.revoicechat.live.stream.representation.StreamRepresentation;

record StreamSession(Streamer streamer, Set<Viewer> viewers) {

  public StreamSession(final Streamer streamer) {
    this(streamer, ConcurrentHashMap.newKeySet());
  }

  public void remove(final Viewer viewer) {
    viewers.remove(viewer);
  }

  public boolean isOwnedBy(UUID user) {
    return streamer.user().equals(user);
  }

  public StreamRepresentation toRepresentation() {
    return new StreamRepresentation(
        streamer.user(),
        streamer.streamName(),
        viewers.stream().map(Viewer::user).toList()
    );
  }

  @Override
  public boolean equals(final Object o) {
    return o instanceof StreamSession that && Objects.equals(streamer, that.streamer);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(streamer);
  }
}