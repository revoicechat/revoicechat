package fr.revoicechat.web.mapper.stubs;

import fr.revoicechat.web.mapper.RepresentationMapper;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EntityMapper implements RepresentationMapper<Entity, EntityDto> {
  @Override
  public EntityDto map(Entity entity) {
    return new EntityDto(entity.name(), false);
  }

  @Override
  public EntityDto mapLight(final Entity entity) {
    return new EntityDto(entity.name(), true);
  }
}