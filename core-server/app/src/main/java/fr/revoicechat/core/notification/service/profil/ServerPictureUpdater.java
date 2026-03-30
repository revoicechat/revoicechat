package fr.revoicechat.core.notification.service.profil;

import static fr.revoicechat.core.notification.service.NotificationUserRetriever.findUserForServer;

import java.util.UUID;

import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.notification.ProfilPictureUpdate;
import fr.revoicechat.core.risk.ServerRiskType;
import fr.revoicechat.notification.Notification;
import fr.revoicechat.risk.service.RiskService;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public final class ServerPictureUpdater implements PictureUpdater<Server> {

  private final RiskService riskService;
  private final EntityManager entityManager;

  public ServerPictureUpdater(final RiskService riskService, EntityManager entityManager) {
    this.riskService = riskService;
    this.entityManager = entityManager;
  }

  @Override
  public Server get(final UUID id) {
    return entityManager.find(Server.class, id);
  }

  @Override
  public void emmit(final Server server) {
    if (riskService.hasRisk(new RiskEntity(server.getId(), null), ServerRiskType.SERVER_UPDATE)) {
      Notification.of(new ProfilPictureUpdate(server.getId())).sendTo(findUserForServer(server.getId()));
    }
  }
}