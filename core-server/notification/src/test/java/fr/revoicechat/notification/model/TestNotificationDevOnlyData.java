package fr.revoicechat.notification.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.revoicechat.notification.stub.NotificationPayloadMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestNotificationDevOnlyData {

  @Test
  void test() throws JsonProcessingException {
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
  void ping() throws JsonProcessingException {
    var data = NotificationData.ping();
    var result = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(data);
    Assertions.assertThat(result).isEqualToNormalizingNewlines("""
        {
          "type" : "PING",
          "data" : { }
        }""");
  }
}