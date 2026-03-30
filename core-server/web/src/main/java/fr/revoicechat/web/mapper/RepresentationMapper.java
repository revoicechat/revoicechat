package fr.revoicechat.web.mapper;

public interface RepresentationMapper<T, U> {

  U map(T t);

  default U mapLight(final T t) {
    return map(t);
  }
}
