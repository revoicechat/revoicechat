package fr.revoicechat.core.repository.impl.message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.repository.impl.room.AccessibleRoomsResolver;
import fr.revoicechat.core.repository.page.PageResult;
import fr.revoicechat.core.technicaldata.message.MessageFilterParams;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@ApplicationScoped
public class MessageSearcher {

  private static final String LIKE = "%";

  private static final String CREATED_DATE = "createdDate";
  private static final String ID = "id";
  private static final String ROOM = "room";
  private static final String USER = "user";
  private static final String TEXT = "text";

  private final EntityManager entityManager;
  private final AccessibleRoomsResolver accessibleRoomsResolver;

  public MessageSearcher(EntityManager entityManager, AccessibleRoomsResolver accessibleRoomsResolver) {
    this.entityManager = entityManager;
    this.accessibleRoomsResolver = accessibleRoomsResolver;
  }

  public PageResult<Message> search(UUID currentUserId, MessageFilterParams params) {
    Set<UUID> accessibleRoomIds = accessibleRoomsResolver.resolve(currentUserId, params.getRoomId());
    return new PageResult<>(
        executeRequest(accessibleRoomIds, params),
        params.getSize(),
        countResult(accessibleRoomIds, params)
    );
  }

  private List<Message> executeRequest(Set<UUID> accessibleRoomIds, MessageFilterParams params) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Message> query = cb.createQuery(Message.class);
    Root<Message> root = query.from(Message.class);

    List<Predicate> predicates = buildPredicates(cb, root, accessibleRoomIds, params);

    query.where(predicates.toArray(new Predicate[0]))
         .orderBy(cb.desc(root.get(CREATED_DATE)));

    return entityManager.createQuery(query)
                        .setFirstResult(params.getPage() * params.getSize())
                        .setMaxResults(params.getSize())
                        .getResultList();
  }

  private Long countResult(Set<UUID> accessibleRoomIds, MessageFilterParams params) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Message> countRoot = countQuery.from(Message.class);
    List<Predicate> countPredicates = buildPredicates(cb, countRoot, accessibleRoomIds, params);

    countQuery.select(cb.count(countRoot))
              .where(countPredicates.toArray(new Predicate[0]));

    return entityManager.createQuery(countQuery).getSingleResult();
  }

  private List<Predicate> buildPredicates(CriteriaBuilder cb,
                                          Root<Message> root,
                                          Set<UUID> accessibleRoomIds,
                                          MessageFilterParams params) {
    List<Predicate> predicates = new ArrayList<>();

    predicates.add(root.get("room").get("id").in(accessibleRoomIds));

    if (params.getRoomId() != null) {
      predicates.add(cb.equal(root.get(ROOM).get(ID), params.getRoomId()));
    }

    if (params.getUserId() != null) {
      predicates.add(cb.equal(root.get(USER).get(ID), params.getUserId()));
    }

    if (isNotNullOrBlank(params.getKeyword())) {
      predicates.add(cb.like(cb.lower(root.get(TEXT)), LIKE + params.getKeyword().toLowerCase() + LIKE));
    }

    if (params.getLastMessage() != null) {
      Subquery<LocalDateTime> subquery = cb.createQuery().subquery(LocalDateTime.class);
      Root<Message> subRoot = subquery.from(Message.class);
      subquery.select(subRoot.get(CREATED_DATE))
              .where(cb.equal(subRoot.get(ID), params.getLastMessage()));
      predicates.add(cb.lessThan(root.get(CREATED_DATE), subquery));
    }

    return predicates;
  }

  private static boolean isNotNullOrBlank(String value) {
    return value != null && !value.isBlank();
  }
}
