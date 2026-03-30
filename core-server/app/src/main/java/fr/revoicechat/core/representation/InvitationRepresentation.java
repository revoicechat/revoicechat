package fr.revoicechat.core.representation;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import fr.revoicechat.core.model.InvitationLinkStatus;
import fr.revoicechat.core.model.InvitationType;

@Schema(name = "InvitationRepresentation",
    description = "Data returned when an invitation is created")
public record InvitationRepresentation(UUID id,
                                       InvitationLinkStatus status,
                                       InvitationType type,
                                       UUID targetedServer) {}
