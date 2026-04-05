package fr.revoicechat.core.representation;

import java.util.List;
import java.util.UUID;

public record NewPrivateMessageRoom(List<UUID> users, String name) {}
