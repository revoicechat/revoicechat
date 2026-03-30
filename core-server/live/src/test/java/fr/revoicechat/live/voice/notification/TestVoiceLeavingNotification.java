package fr.revoicechat.live.voice.notification;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.revoicechat.notification.model.NotificationData;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestVoiceLeavingNotification {

  @Test
  void test() throws JsonProcessingException {
    var userId = UUID.fromString("ec55c4b3-5474-43d0-8f4d-953e0a08e228");
    var roomId = UUID.fromString("d081700a-0c4f-430a-85ea-5e82ce485ce7");
    var data = new NotificationData(new VoiceLeavingNotification(userId, roomId));
    var result = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(data);
    Assertions.assertThat(result).isEqualToNormalizingNewlines("""
                                                                   {
                                                                     "type" : "VOICE_LEAVING",
                                                                     "data" : {
                                                                       "user" : "ec55c4b3-5474-43d0-8f4d-953e0a08e228",
                                                                       "roomId" : "d081700a-0c4f-430a-85ea-5e82ce485ce7"
                                                                     }
                                                                   }""");
  }
}