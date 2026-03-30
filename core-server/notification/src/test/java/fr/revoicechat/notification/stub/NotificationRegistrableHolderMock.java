package fr.revoicechat.notification.stub;

import java.util.UUID;

import fr.revoicechat.notification.NotificationRegistrableHolder;
import fr.revoicechat.notification.model.NotificationRegistrable;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotificationRegistrableHolderMock implements NotificationRegistrableHolder {

  private final SecurityIdentity securityIdentity;

  public NotificationRegistrableHolderMock(final SecurityIdentity securityIdentity) {
    this.securityIdentity = securityIdentity;
  }

  @Override
  public NotificationRegistrable get() {
    var id = UUID.fromString(securityIdentity.getPrincipal().getName());
    return NotificationRegistrable.forId(id);
  }
}
