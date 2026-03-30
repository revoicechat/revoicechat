package fr.revoicechat.core.mapper;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.revoicechat.core.model.MediaData;
import fr.revoicechat.core.representation.MediaDataRepresentation;
import fr.revoicechat.web.mapper.RepresentationMapper;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

@Unremovable
@ApplicationScoped
public class MediaDataMapper implements RepresentationMapper<MediaData, MediaDataRepresentation> {

  @ConfigProperty(name = "revoicechat.global.media-server-url")
  String mediaServerUrl;

  @Override
  public MediaDataRepresentation map(final MediaData media) {
    return new MediaDataRepresentation(
        media.getId(),
        media.getName(),
        mediaServerUrl + "/" + media.getName(),
        media.getOrigin(),
        media.getStatus(),
        media.getType()
    );
  }
}
