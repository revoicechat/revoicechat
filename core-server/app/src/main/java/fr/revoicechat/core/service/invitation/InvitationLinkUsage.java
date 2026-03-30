package fr.revoicechat.core.service.invitation;

import fr.revoicechat.core.model.InvitationLink;
import fr.revoicechat.core.model.User;

public interface InvitationLinkUsage {
  void use(InvitationLink invitationLink, User user);

  default void use(InvitationLink invitationLink) {
    use(invitationLink, null);
  }
}
