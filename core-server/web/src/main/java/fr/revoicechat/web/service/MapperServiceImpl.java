package fr.revoicechat.web.service;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import fr.revoicechat.web.mapper.RepresentationMapper;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@Unremovable
@ApplicationScoped
public class MapperServiceImpl implements MapperService {

  private final Map<Class<?>, RepresentationMapper<?, ?>> mapperRegistry = new HashMap<>();

  @Inject
  public MapperServiceImpl(final Instance<RepresentationMapper<?, ?>> mappers) {
    Optional.ofNullable(mappers).stream()
            .flatMap(Instance::stream)
            .forEach(mapper -> {
              Class<?> sourceType = resolveSourceType(mapper.getClass());
              mapperRegistry.put(sourceType, mapper);
            });
  }

  @Override
  public <T, U> U map(T source) {
    return this.<T, U>findMapper(source.getClass()).map(source);
  }

  @Override
  public <T, U> U mapLight(T source) {
    return this.<T, U>findMapper(source.getClass()).mapLight(source);
  }

  @SuppressWarnings("unchecked")
  private <T, U> RepresentationMapper<T, U> findMapper(Class<?> sourceClass) {
    RepresentationMapper<?, ?> mapper = mapperRegistry.get(sourceClass);
    if (mapper == null) {
      throw new IllegalArgumentException("No mapper found for: " + sourceClass.getSimpleName());
    }
    return (RepresentationMapper<T, U>) mapper;
  }

  private Class<?> resolveSourceType(Class<?> cls) {
    if (cls.getName().contains("_ClientProxy")) {
      return resolveSourceType(cls.getSuperclass());
    }
    return Stream.of(cls.getGenericInterfaces())
                 .filter(ParameterizedType.class::isInstance)
                 .map(ParameterizedType.class::cast)
                 .filter(pt -> pt.getRawType().equals(RepresentationMapper.class))
                 .findFirst()
                 .map(pt -> (Class<?>) pt.getActualTypeArguments()[0])
                 .orElseThrow();
  }
}