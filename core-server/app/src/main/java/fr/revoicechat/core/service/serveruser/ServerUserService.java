package fr.revoicechat.core.service.serveruser;

import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.model.ServerUser;

@FunctionalInterface
public interface ServerUserService {
  ServerUser join(Server server);
}
