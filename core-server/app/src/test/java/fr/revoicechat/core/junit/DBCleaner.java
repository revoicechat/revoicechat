package fr.revoicechat.core.junit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
class DBCleaner {
  @PersistenceContext
  EntityManager entityManager;

  void clean() {
    entityManager.flush();
    entityManager.clear();
    entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").asStatement().execute();
    entityManager.getMetamodel().getEntities().forEach(entityType -> {
      String entityName = entityType.getName();
      entityManager.createQuery("DELETE FROM " + entityName).asStatement().execute();
    });
    entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").asStatement().execute();
    entityManager.flush();
  }
}