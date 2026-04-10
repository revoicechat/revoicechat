package fr.revoicechat.security.repository.impl;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.security.model.UserRecoverCode;
import fr.revoicechat.security.repository.UserRecoverCodeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class UserRecoverCodeRepositoryImpl implements UserRecoverCodeRepository {
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<UserRecoverCode> findByUser(UUID userId) {
    return entityManager.createQuery("""
                            select codes
                            from UserRecoverCode codes
                            where codes.userId = :userId""", UserRecoverCode.class)
                        .setParameter("userId", userId)
                        .getResultList();
  }
}
