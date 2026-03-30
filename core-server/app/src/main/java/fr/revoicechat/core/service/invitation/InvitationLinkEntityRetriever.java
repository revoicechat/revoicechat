package fr.revoicechat.core.service.invitation;

import java.util.UUID;

import fr.revoicechat.core.model.InvitationLink;

public interface InvitationLinkEntityRetriever {
  InvitationLink getEntity(UUID invitationId);
}
