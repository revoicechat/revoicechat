package fr.revoicechat.core.repository;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.model.Emote;

public interface EmoteRepository {
  Stream<Emote> findByEntity(UUID entity);

  Stream<Emote> findGlobal();
}
