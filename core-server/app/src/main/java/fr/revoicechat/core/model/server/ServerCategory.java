package fr.revoicechat.core.model.server;

import java.util.List;

public record ServerCategory(
    String name,
    List<ServerItem> items
) implements ServerItem {}
