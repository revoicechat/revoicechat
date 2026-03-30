package fr.revoicechat.core.repository;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.model.InvitationLink;

public interface InvitationLinkRepository {

  Stream<InvitationLink> getAllFromUser(UUID user);

  Stream<InvitationLink> getAllFromServer(UUID server);

  Stream<InvitationLink> allApplicationInvitations();
}
