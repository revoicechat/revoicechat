package fr.revoicechat.core.service.message.textextractor;

import static fr.revoicechat.core.representation.message.PatternType.ROLE_MENTION;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.representation.message.MessageMention;
import fr.revoicechat.core.representation.message.TextPattern;
import fr.revoicechat.risk.model.ServerRoles;
import fr.revoicechat.risk.model.UserRoleMembership;
import fr.revoicechat.security.UserHolder;
import io.quarkus.arc.Unremovable;

@Unremovable
@ApplicationScoped
class RoleMentionExtractor implements TextPatternExtractor {

  private static final Pattern ROLE_MENTION_REGEXP = Pattern.compile("<@&roleId:(?<id>[0-9a-fA-F\\-]{36})>");

  private final UserHolder userHolder;
  private final EntityManager entityManager;

  RoleMentionExtractor(final UserHolder userHolder, EntityManager entityManager) {
    this.userHolder = userHolder;
    this.entityManager = entityManager;
  }

  @Override
  public List<TextPattern> extract(final Message message) {
    var roles = rolesId();
    List<TextPattern> mentions = new ArrayList<>();
    Matcher matcher = ROLE_MENTION_REGEXP.matcher(message.getText());
    while (matcher.find()) {
      toMention(matcher, roles).ifPresent(mentions::add);
    }
    return mentions;
  }

  private Optional<TextPattern> toMention(Matcher matcher, final List<UUID> roles) {
    try {
      UUID id = UUID.fromString(matcher.group("id"));
      return Optional.ofNullable(entityManager.find(ServerRoles.class, id))
                     .map(ServerRoles::getName)
                     .map(name -> new MessageMention(id, name, roles.contains(id)))
                     .map(mention -> new TextPattern(matcher.group(), ROLE_MENTION, mention));
    } catch (IllegalArgumentException _) {
      return Optional.empty();
    }
  }

  private List<UUID> rolesId() {
    var membership = entityManager.find(UserRoleMembership.class, userHolder.getId());
    return membership.getServerRoles().stream().map(ServerRoles::getId).toList();
  }
}
