package fr.revoicechat.core.representation;

import java.util.UUID;

import fr.revoicechat.core.model.FileType;
import fr.revoicechat.core.model.MediaDataStatus;
import fr.revoicechat.core.model.MediaOrigin;

public record MediaDataRepresentation(
    UUID id,
    String name,
    String url,
    MediaOrigin origin,
    MediaDataStatus status,
    FileType type) {}
