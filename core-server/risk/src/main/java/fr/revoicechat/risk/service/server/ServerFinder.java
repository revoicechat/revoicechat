package fr.revoicechat.risk.service.server;

import java.util.UUID;

public interface ServerFinder {

  void existsOrThrow(UUID id);
}
