package fr.revoicechat.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestMediaData {
  @Test
  @SuppressWarnings({ "java:S5838", "java:S5863", "EqualsWithItself" })
  void test() {
    var id1 = UUID.randomUUID();
    var media1 = new MediaData();
    media1.setId(id1);
    var media2 = new MediaData();
    media2.setId(id1);
    var media3 = new MediaData();
    media3.setId(UUID.randomUUID());
    assertThat(media1).isEqualTo(media1)
                      .isEqualTo(media2)
                      .hasSameHashCodeAs(media2)
                      .isNotEqualTo(media3)
                      .isNotEqualTo(null)
                      .isNotEqualTo(new Object());
  }
}