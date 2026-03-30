package fr.revoicechat.live.common.service;

import java.util.UUID;

import fr.revoicechat.risk.technicaldata.RiskEntity;

public interface RoomRisksEntityRetriever {

  RiskEntity get(UUID roomId);
}
