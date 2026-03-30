package fr.revoicechat.live.stream.service;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.live.stream.representation.StreamRepresentation;

public interface StreamRetriever {

  List<StreamRepresentation> fetch(UUID userId);
}
