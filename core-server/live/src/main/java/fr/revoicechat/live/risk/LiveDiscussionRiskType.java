package fr.revoicechat.live.risk;

import fr.revoicechat.risk.type.RiskCategory;
import fr.revoicechat.risk.type.RoomEntityRiskType;

@RiskCategory("LIVE_DISCUSSION_RISK_TYPE")
public enum LiveDiscussionRiskType implements RoomEntityRiskType {
  JOIN_VOICE_ROOM,
  SEND_IN_VOICE_ROOM,
  RECEIVE_IN_VOICE_ROOM,
  STREAM_IN_VOICE_ROOM,
  WATCH_STREAM_IN_VOICE_ROOM,
}
