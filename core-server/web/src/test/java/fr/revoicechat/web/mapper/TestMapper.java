package fr.revoicechat.web.mapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.revoicechat.web.service.MapperService;

class TestMapper {

  @BeforeEach
  void setUp() {
    Mapper.cleanMapperService();
  }

  @Test
  void testMap() {
    // Given
    var service = new MapperServiceMock();
    Mapper.setMapperService(service);
    // When
    Mapper.map(new Object());
    Mapper.map(new Object());
    Mapper.map(new Object());
    Mapper.mapLight(new Object());
    // Then
    Assertions.assertThat(service.map).isEqualTo(3);
    Assertions.assertThat(service.mapLight).isEqualTo(1);
  }

  private static class MapperServiceMock implements MapperService {
    int map = 0;
    int mapLight = 0;

    @Override
    public <T, U> U map(final T source) {
      map++;
      return null;
    }

    @Override
    public <T, U> U mapLight(final T source) {
      mapLight++;
      return null;
    }
  }
}