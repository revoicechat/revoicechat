package fr.revoicechat.core.repository.impl;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.model.InvitationLink;
import fr.revoicechat.core.model.InvitationType;
import fr.revoicechat.core.repository.InvitationLinkRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class InvitationLinkRepositoryImpl implements InvitationLinkRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Stream<InvitationLink> getAllFromUser(final UUID user) {
    return entityManager.createQuery("select i from InvitationLink i where i.sender.id = :user", InvitationLink.class)
                        .setParameter("user", user)
                        .getResultStream();
  }

  @Override
  public Stream<InvitationLink> getAllFromServer(final UUID server) {
    return entityManager.createQuery("select i from InvitationLink i where i.targetedServer.id = :server", InvitationLink.class)
                        .setParameter("server", server)
                        .getResultStream();
  }

  @Override
  public Stream<InvitationLink> allApplicationInvitations() {
    return entityManager.createQuery("select i from InvitationLink i where i.type = :type", InvitationLink.class)
                        .setParameter("type", InvitationType.APPLICATION_JOIN)
                        .getResultStream();
  }
}
