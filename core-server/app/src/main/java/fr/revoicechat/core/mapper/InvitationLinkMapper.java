package fr.revoicechat.core.mapper;

import java.util.Optional;

import fr.revoicechat.core.model.InvitationLink;
import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.representation.InvitationRepresentation;
import fr.revoicechat.web.mapper.RepresentationMapper;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

@Unremovable
@ApplicationScoped
public class InvitationLinkMapper implements RepresentationMapper<InvitationLink, InvitationRepresentation> {

  @Override
  public InvitationRepresentation map(final InvitationLink link) {
    return new InvitationRepresentation(link.getId(),
        link.getStatus(),
        link.getType(),
        Optional.ofNullable(link.getTargetedServer())
                .map(Server::getId)
                .orElse(null));
  }
}
