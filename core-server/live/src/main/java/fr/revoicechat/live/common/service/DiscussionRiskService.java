package fr.revoicechat.live.common.service;

import static fr.revoicechat.live.risk.LiveDiscussionRiskType.*;

import java.util.UUID;
import jakarta.enterprise.context.ApplicationScoped;

import fr.revoicechat.live.risk.LiveDiscussionRisks;
import fr.revoicechat.moderation.model.SanctionType;
import fr.revoicechat.risk.service.RiskService;
import fr.revoicechat.risk.type.RiskType;

@ApplicationScoped
public class DiscussionRiskService {

  private final RoomRisksEntityRetriever roomRisksEntityRetriever;
  private final RiskService riskService;

  public DiscussionRiskService(RoomRisksEntityRetriever roomRisksEntityRetriever, RiskService riskService) {
    this.roomRisksEntityRetriever = roomRisksEntityRetriever;
    this.riskService = riskService;
  }

  public LiveDiscussionRisks getVoiceRisks(UUID roomId, UUID userId) {
    return get(roomId, userId, SEND_IN_VOICE_ROOM, RECEIVE_IN_VOICE_ROOM);
  }

  public LiveDiscussionRisks getStreamRisks(UUID roomId, UUID userId) {
    return get(roomId, userId, STREAM_IN_VOICE_ROOM, WATCH_STREAM_IN_VOICE_ROOM);
  }

  private LiveDiscussionRisks get(UUID roomId, UUID userId, RiskType send, RiskType receive) {
    var riskEntity = roomRisksEntityRetriever.get(roomId);
    return new LiveDiscussionRisks(
        riskService.hasRisk(userId, riskEntity, JOIN_VOICE_ROOM, SanctionType.VOICE_TIME_OUT),
        riskService.hasRisk(userId, riskEntity, send,            SanctionType.VOICE_TIME_OUT),
        riskService.hasRisk(userId, riskEntity, receive,         SanctionType.VOICE_TIME_OUT)
    );
  }
}
