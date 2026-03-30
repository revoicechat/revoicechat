package fr.revoicechat.web.mapper.error;

import static org.assertj.core.api.Assertions.*;

import java.io.IOError;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestErrorMapperUtils {

  @Test
  void test() {
    assertThat(ErrorMapperUtils.fetchForbiddenAccessFile("/static/generic-error-template.json"))
        .isEqualToNormalizingNewlines("""
                                          {
                                            "error": "%s",
                                            "message": "%s",
                                            "swaggerDoc": "/api/q/swagger-ui"
                                          }""");
  }

  @Test
  void testError() {
    assertThatThrownBy(() -> ErrorMapperUtils.fetchForbiddenAccessFile("/static/unknown")).isInstanceOf(IOError.class);
  }
}