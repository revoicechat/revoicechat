package fr.revoicechat.live.stream.socket;

import java.util.UUID;

import fr.revoicechat.live.common.socket.SessionHolder;
import fr.revoicechat.live.risk.LiveDiscussionRisks;

interface StreamAgent extends SessionHolder {
  UUID user();
  LiveDiscussionRisks risks();
}
