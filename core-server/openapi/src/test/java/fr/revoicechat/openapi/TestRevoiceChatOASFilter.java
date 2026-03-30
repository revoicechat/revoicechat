package fr.revoicechat.openapi;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestRevoiceChatOASFilter {

  @Test
  void testConstructor() {
    assertThatCode(RevoiceChatOASFilter::new).doesNotThrowAnyException();
  }

  @Test
  void testFilterSchema() {
    var schemaFilterer = new SchemaFiltererMock();
    var openAPIFilterer = new OpenAPIFiltererMock();
    new RevoiceChatOASFilter(List.of(schemaFilterer), List.of(openAPIFilterer)).filterSchema(null);
    assertThat(schemaFilterer.called).isTrue();
    assertThat(openAPIFilterer.called).isFalse();
  }

  @Test
  void testFilterOpenAPI() {
    var schemaFilterer = new SchemaFiltererMock();
    var openAPIFilterer = new OpenAPIFiltererMock();
    new RevoiceChatOASFilter(List.of(schemaFilterer), List.of(openAPIFilterer)).filterOpenAPI(null);
    assertThat(schemaFilterer.called).isFalse();
    assertThat(openAPIFilterer.called).isTrue();
  }

  private static class SchemaFiltererMock implements SchemaFilterer {
    boolean called = false;

    @Override
    public Schema filterSchema(final Schema schema) {
      called = true;
      return schema;
    }
  }

  private static class OpenAPIFiltererMock implements OpenAPIFilterer {
    boolean called = false;

    @Override
    public void filterOpenAPI(final OpenAPI openAPI) {
      called = true;
    }
  }
}