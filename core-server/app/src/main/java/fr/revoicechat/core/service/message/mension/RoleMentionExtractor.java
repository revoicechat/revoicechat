package fr.revoicechat.core.service.message.mension;

import static fr.revoicechat.core.representation.message.MessageMention.MentionType.ROLE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.representation.message.MessageMention;
import fr.revoicechat.risk.model.ServerRoles;
import fr.revoicechat.risk.model.UserRoleMembership;
import fr.revoicechat.security.UserHolder;
import io.quarkus.arc.Unremovable;

@Unremovable
@ApplicationScoped
public class RoleMentionExtractor implements MentionExtractor {

  private static final Pattern ROLE_MENTION = Pattern.compile("<@&roleId:(?<id>[0-9a-fA-F\\-]{36})>");

  private final UserHolder userHolder;
  private final EntityManager entityManager;

  public RoleMentionExtractor(final UserHolder userHolder, EntityManager entityManager) {
    this.userHolder = userHolder;
    this.entityManager = entityManager;}

  @Override
  public Map<String, MessageMention> extract(final Message message) {
    var roles = rolesId();
    Map<String, MessageMention> mentions = new HashMap<>();
    Matcher matcher = ROLE_MENTION.matcher(message.getText());
    while (matcher.find()) {
      toMention(matcher, roles).ifPresent(m -> mentions.put(matcher.group(), m));
    }
    return mentions;
  }

  private Optional<MessageMention> toMention(Matcher matcher, final List<UUID> roles) {
    try {
      UUID id = UUID.fromString(matcher.group("id"));
      return Optional.ofNullable(entityManager.find(ServerRoles.class, id))
                     .map(ServerRoles::getName)
                     .map(name -> new MessageMention(id, ROLE, name, roles.contains(id)));
    } catch (IllegalArgumentException _) {
      return Optional.empty();
    }
  }

  private List<UUID> rolesId() {
    var membership = entityManager.find(UserRoleMembership.class, userHolder.getId());
    return membership.getServerRoles().stream().map(ServerRoles::getId).toList();
  }
}
