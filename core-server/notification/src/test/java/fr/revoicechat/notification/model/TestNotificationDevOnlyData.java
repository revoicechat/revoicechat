package fr.revoicechat.notification.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.revoicechat.notification.stub.NotificationPayloadMock;
import io.quarkus.test.junit.QuarkusTest;
import tools.jackson.databind.ObjectMapper;

@QuarkusTest
class TestNotificationDevOnlyData {

  @Test
  void test() {
    var data = new NotificationData(new NotificationPayloadMock("test"));
    var result = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(data);
    Assertions.assertThat(result).isEqualToNormalizingNewlines("""
        {
          "type" : "MOCK",
          "data" : {
            "name" : "test"
          }
        }""");
  }

  @Test
  void ping() {
    var data = NotificationData.ping();
    var result = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(data);
    Assertions.assertThat(result).isEqualToNormalizingNewlines("""
        {
          "type" : "PING",
          "data" : { }
        }""");
  }
}