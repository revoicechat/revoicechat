package fr.revoicechat.web.mapper;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import fr.revoicechat.web.mapper.stubs.Entity;
import fr.revoicechat.web.mapper.stubs.EntityDto;
import fr.revoicechat.web.service.MapperService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@QuarkusTest
class TestMapperService {

  record User(String name) {}
  record UserDto(String name) {}

  @ApplicationScoped
  static class UserMapper implements RepresentationMapper<User, UserDto> {
    @Override
    public UserDto map(User user) {
      return new UserDto(user.name());
    }
  }

  @Inject
  MapperService mapperService;

  @Test
  void shouldMapUser() {
    UserDto result = mapperService.map(new User("Alice"));
    assertThat(result.name()).isEqualTo("Alice");
  }

  @Test
  void shouldMapLightUser() {
    UserDto result = mapperService.mapLight(new User("Alice"));
    assertThat(result.name()).isEqualTo("Alice");
  }

  @Test
  void shouldMapEntity() {
    EntityDto result = mapperService.map(new Entity("Value"));
    assertThat(result.name()).isEqualTo("Value");
    assertThat(result.light()).isFalse();
  }

  @Test
  void shouldMapLightEntity() {
    EntityDto result = mapperService.mapLight(new Entity("Value1"));
    assertThat(result.name()).isEqualTo("Value1");
    assertThat(result.light()).isTrue();
  }

  @Test
  void shouldMapUsingStatic() {
    EntityDto result1 = Mapper.map(new Entity("Value2"));
    assertThat(result1.name()).isEqualTo("Value2");

    EntityDto result2 = Mapper.map(new Entity("Value3"));
    assertThat(result2.name()).isEqualTo("Value3");
  }

  @Test
  void shouldMapLightUsingStatic() {
    EntityDto result1 = Mapper.mapLight(new Entity("Value2"));
    assertThat(result1.name()).isEqualTo("Value2");
    assertThat(result1.light()).isTrue();

    EntityDto result2 = Mapper.mapLight(new Entity("Value3"));
    assertThat(result2.name()).isEqualTo("Value3");
    assertThat(result2.light()).isTrue();
  }

  @Test
  void shouldThrowWhenNoMapperFound() {
    assertThatThrownBy(() -> mapperService.map("unmapped"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("String");
  }
}