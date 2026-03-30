package fr.revoicechat.notification.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.revoicechat.notification.model.NotificationPayload;
import fr.revoicechat.notification.model.NotificationType;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

@QuarkusTest
class TestNotificationOpenAPIFilterer {

  @Test
  void testNotificationPayloads() {
    String json = RestAssured.get("/q/openapi?format=json").asPrettyString();
    JsonPath path = new JsonPath(json);

    List<String> type = getList(path, "type.enum");
    List<String> data = getList(path, "data.oneOf.$ref");
    assertThat(type).hasSameSizeAs(data);
    assertThat(data).contains("#/components/schemas/NotificationOpenAPI");
    assertThat(type).contains("TEST_OPEN_API");
  }

  private static List<String> getList(final JsonPath path, String property) {
    return path.getList("paths.'/api/sse'.get.responses.200.content.\"application/json\".schema.properties." + property);
  }

  @NotificationType(name = "TEST_OPEN_API")
  @SuppressWarnings("unused") // here for reflection test purpose
  public record NotificationOpenAPI(String name) implements NotificationPayload {}
}
