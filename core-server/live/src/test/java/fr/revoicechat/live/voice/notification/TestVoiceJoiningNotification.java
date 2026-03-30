package fr.revoicechat.live.voice.notification;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.revoicechat.notification.model.NotificationData;
import fr.revoicechat.notification.data.UserNotificationRepresentation;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestVoiceJoiningNotification {

  @Test
  void test() throws JsonProcessingException {
    var userId = UUID.fromString("ec55c4b3-5474-43d0-8f4d-953e0a08e228");
    var roomId = UUID.fromString("d081700a-0c4f-430a-85ea-5e82ce485ce7");
    var data = new NotificationData(new VoiceJoiningNotification(new UserNotificationRepresentation(userId, "test"), roomId));
    var result = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(data);
    Assertions.assertThat(result).isEqualToNormalizingNewlines("""
                                                                   {
                                                                     "type" : "VOICE_JOINING",
                                                                     "data" : {
                                                                       "user" : {
                                                                         "id" : "ec55c4b3-5474-43d0-8f4d-953e0a08e228",
                                                                         "displayName" : "test"
                                                                       },
                                                                       "roomId" : "d081700a-0c4f-430a-85ea-5e82ce485ce7"
                                                                     }
                                                                   }""");
  }
}