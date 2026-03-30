package fr.revoicechat.core.stub;

import java.util.List;
import java.util.Map;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.ConnectionConsumer;
import jakarta.persistence.ConnectionFunction;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FindOption;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockOption;
import jakarta.persistence.Query;
import jakarta.persistence.RefreshOption;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.TypedQueryReference;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaSelect;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;

/**
 * {@link EntityManager} that do nothing.
 */
public abstract class EntityManagerMock implements EntityManager {
  @Override
  public void persist(final Object o) {
    // nothing there
  }

  @Override
  public <T> T merge(final T t) {
    return null;
  }

  @Override
  public void remove(final Object o) {
    // nothing there
  }

  @Override
  public <T> T find(final Class<T> aClass, final Object o) {
    return null;
  }

  @Override
  public <T> T find(final Class<T> aClass, final Object o, final Map<String, Object> map) {
    return null;
  }

  @Override
  public <T> T find(final Class<T> aClass, final Object o, final LockModeType lockModeType) {
    return null;
  }

  @Override
  public <T> T find(final Class<T> aClass, final Object o, final LockModeType lockModeType, final Map<String, Object> map) {
    return null;
  }

  @Override
  public <T> T find(final Class<T> aClass, final Object o, final FindOption... findOptions) {
    return null;
  }

  @Override
  public <T> T find(final EntityGraph<T> entityGraph, final Object o, final FindOption... findOptions) {
    return null;
  }

  @Override
  public <T> T getReference(final Class<T> aClass, final Object o) {
    return null;
  }

  @Override
  public <T> T getReference(final T t) {
    return null;
  }

  @Override
  public void flush() {
    // nothing there
  }

  @Override
  public void setFlushMode(final FlushModeType flushModeType) {
    // nothing there
  }

  @Override
  public FlushModeType getFlushMode() {
    return null;
  }

  @Override
  public void lock(final Object o, final LockModeType lockModeType) {
    // nothing there
  }

  @Override
  public void lock(final Object o, final LockModeType lockModeType, final Map<String, Object> map) {
    // nothing there
  }

  @Override
  public void lock(final Object o, final LockModeType lockModeType, final LockOption... lockOptions) {
    // nothing there
  }

  @Override
  public void refresh(final Object o) {
    // nothing there
  }

  @Override
  public void refresh(final Object o, final Map<String, Object> map) {
    // nothing there
  }

  @Override
  public void refresh(final Object o, final LockModeType lockModeType) {
    // nothing there
  }

  @Override
  public void refresh(final Object o, final LockModeType lockModeType, final Map<String, Object> map) {
    // nothing there
  }

  @Override
  public void refresh(final Object o, final RefreshOption... refreshOptions) {
    // nothing there
  }

  @Override
  public void clear() {
    // nothing there
  }

  @Override
  public void detach(final Object o) {
    // nothing there
  }

  @Override
  public boolean contains(final Object o) {
    return false;
  }

  @Override
  public LockModeType getLockMode(final Object o) {
    return null;
  }

  @Override
  public void setCacheRetrieveMode(final CacheRetrieveMode cacheRetrieveMode) {
    // nothing there
  }

  @Override
  public void setCacheStoreMode(final CacheStoreMode cacheStoreMode) {
    // nothing there
  }

  @Override
  public CacheRetrieveMode getCacheRetrieveMode() {
    return null;
  }

  @Override
  public CacheStoreMode getCacheStoreMode() {
    return null;
  }

  @Override
  public void setProperty(final String s, final Object o) {
    // nothing there
  }

  @Override
  public Map<String, Object> getProperties() {
    return Map.of();
  }

  @Override
  public Query createQuery(final String s) {
    return null;
  }

  @Override
  public <T> TypedQuery<T> createQuery(final CriteriaQuery<T> criteriaQuery) {
    return null;
  }

  @Override
  public <T> TypedQuery<T> createQuery(final CriteriaSelect<T> criteriaSelect) {
    return null;
  }

  @Override
  public Query createQuery(final CriteriaUpdate<?> criteriaUpdate) {
    return null;
  }

  @Override
  public Query createQuery(final CriteriaDelete<?> criteriaDelete) {
    return null;
  }

  @Override
  public <T> TypedQuery<T> createQuery(final String s, final Class<T> aClass) {
    return null;
  }

  @Override
  public Query createNamedQuery(final String s) {
    return null;
  }

  @Override
  public <T> TypedQuery<T> createNamedQuery(final String s, final Class<T> aClass) {
    return null;
  }

  @Override
  public <T> TypedQuery<T> createQuery(final TypedQueryReference<T> typedQueryReference) {
    return null;
  }

  @Override
  public Query createNativeQuery(final String s) {
    return null;
  }

  @Override
  public <T> Query createNativeQuery(final String s, final Class<T> aClass) {
    return null;
  }

  @Override
  public Query createNativeQuery(final String s, final String s1) {
    return null;
  }

  @Override
  public StoredProcedureQuery createNamedStoredProcedureQuery(final String s) {
    return null;
  }

  @Override
  public StoredProcedureQuery createStoredProcedureQuery(final String s) {
    return null;
  }

  @Override
  public StoredProcedureQuery createStoredProcedureQuery(final String s, final Class<?>... classes) {
    return null;
  }

  @Override
  public StoredProcedureQuery createStoredProcedureQuery(final String s, final String... strings) {
    return null;
  }

  @Override
  public void joinTransaction() {
    // nothing there
  }

  @Override
  public boolean isJoinedToTransaction() {
    return false;
  }

  @Override
  public <T> T unwrap(final Class<T> aClass) {
    return null;
  }

  @Override
  public Object getDelegate() {
    return null;
  }

  @Override
  public void close() {
    // nothing there
  }

  @Override
  public boolean isOpen() {
    return false;
  }

  @Override
  public EntityTransaction getTransaction() {
    return null;
  }

  @Override
  public EntityManagerFactory getEntityManagerFactory() {
    return null;
  }

  @Override
  public CriteriaBuilder getCriteriaBuilder() {
    return null;
  }

  @Override
  public Metamodel getMetamodel() {
    return null;
  }

  @Override
  public <T> EntityGraph<T> createEntityGraph(final Class<T> aClass) {
    return null;
  }

  @Override
  public EntityGraph<?> createEntityGraph(final String s) {
    return null;
  }

  @Override
  public EntityGraph<?> getEntityGraph(final String s) {
    return null;
  }

  @Override
  public <T> List<EntityGraph<? super T>> getEntityGraphs(final Class<T> aClass) {
    return List.of();
  }

  @Override
  public <C> void runWithConnection(final ConnectionConsumer<C> connectionConsumer) {
    // nothing there
  }

  @Override
  public <C, T> T callWithConnection(final ConnectionFunction<C, T> connectionFunction) {
    return null;
  }
}
