package fr.revoicechat.live.stream.representation;

import java.util.List;
import java.util.UUID;

public record StreamRepresentation(
    UUID user,
    String streamName,
    List<UUID> viewers
) {}