package fr.revoicechat.core.mapper;

import java.util.ArrayList;

import fr.revoicechat.core.model.Emote;
import fr.revoicechat.core.representation.EmoteRepresentation;
import fr.revoicechat.web.mapper.RepresentationMapper;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

@Unremovable
@ApplicationScoped
public class EmoteMapper implements RepresentationMapper<Emote, EmoteRepresentation> {

  @Override
  public EmoteRepresentation map(final Emote emote) {
    return new EmoteRepresentation(
        emote.getId(),
        emote.getContent(),
        new ArrayList<>(emote.getKeywords())
    );
  }
}
