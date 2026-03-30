package fr.revoicechat.web.mapper;

import java.util.Collection;
import java.util.List;

import fr.revoicechat.web.service.MapperService;
import jakarta.enterprise.inject.spi.CDI;

public class Mapper {
  private static final ThreadLocal<MapperService> holder = new ThreadLocal<>();

  public static <T, U> List<U> mapAll(Collection<T> source) {
    return source.stream()
                 .<U>map(Mapper::map)
                 .toList();
  }

  public static <T, U> List<U> mapLightAll(Collection<T> source) {
    return source.stream()
                 .<U>map(Mapper::mapLight)
                 .toList();
  }

  public static <T, U> U map(T source) {
    return getMapperService().map(source);
  }

  public static <T, U> U mapLight(T source) {
    return getMapperService().mapLight(source);
  }


  private static MapperService getMapperService() {
    MapperService sender = holder.get();
    if (sender == null) {
      sender = CDI.current().select(MapperService.class).get();
      holder.set(sender);
    }
    return sender;
  }

  static void setMapperService(MapperService mapperService) {
    holder.set(mapperService);
  }

  static void cleanMapperService() {
    holder.remove();
  }
}
