package fr.revoicechat.security.repository.impl;

import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.security.repository.AuthenticatedUserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class AuthenticatedUserRepositoryImpl implements AuthenticatedUserRepository {
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public AuthenticatedUser findByLogin(final String login) {
    return entityManager.createQuery("""
                            select u
                            from AuthenticatedUser u
                            where u.login = :login""", AuthenticatedUser.class)
                        .setParameter("login", login)
                        .getSingleResultOrNull();
  }
}
