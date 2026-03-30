package fr.revoicechat.live.voice.notification;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import fr.revoicechat.notification.model.NotificationPayload;
import fr.revoicechat.notification.model.NotificationType;

@NotificationType(name = "VOICE_LEAVING")
@Schema(description = "Voice leaving notification", examples = """
    {
      "type" : "VOICE_LEAVING",
      "data" : {
        "user" : "B0faEeD2-f2Ac-DCFD-eDda-b0c4A022636e",
        "roomId" : "0a9B9dC8-1feb-6262-4f69-BABd478482Df"
      }
    }""")
public record VoiceLeavingNotification(UUID user, UUID roomId) implements NotificationPayload {}
