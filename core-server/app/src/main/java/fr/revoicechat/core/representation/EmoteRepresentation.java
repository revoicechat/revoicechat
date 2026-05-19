package fr.revoicechat.core.representation;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.core.representation.message.TextPatternData;

public record EmoteRepresentation(UUID id, String name, List<String> keywords) implements TextPatternData {}
