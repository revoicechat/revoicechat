package fr.revoicechat.core.mapper;

import fr.revoicechat.core.representation.OpenGraphSchemaHolder;
import fr.revoicechat.opengraph.OpenGraphExtractor;
import fr.revoicechat.opengraph.OpenGraphSchema;
import fr.revoicechat.web.mapper.RepresentationMapper;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

@Unremovable
@ApplicationScoped
public class OpenGraphSchemaMapper implements RepresentationMapper<OpenGraphSchemaHolder, OpenGraphSchema> {

  private final OpenGraphExtractor openGraphExtractor;

  public OpenGraphSchemaMapper(final OpenGraphExtractor openGraphExtractor) {
    this.openGraphExtractor = openGraphExtractor;
  }

  @Override
  public OpenGraphSchema map(final OpenGraphSchemaHolder message) {
    return openGraphExtractor.extract(message.text());
  }
}
