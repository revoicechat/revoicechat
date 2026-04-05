package fr.revoicechat.moderation.service;

import static fr.revoicechat.moderation.model.SanctionType.BAN;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import fr.revoicechat.moderation.model.Sanction;
import fr.revoicechat.moderation.model.SanctionType;
import fr.revoicechat.moderation.repository.SanctionRepository;
import fr.revoicechat.security.UserHolder;

@ApplicationScoped
public class SanctionService {

  private final UserHolder userHolder;
  private final SanctionRepository sanctionRepository;

  public SanctionService(UserHolder userHolder, SanctionRepository sanctionRepository) {
    this.userHolder = userHolder;
    this.sanctionRepository = sanctionRepository;
  }

  @Transactional
  public List<Sanction> getSanctions(UUID userId) {
    return activeSanctions(sanctionRepository.getSanctions(userId));
  }

  @Transactional
  public List<Sanction> getSanctions(UUID userId, UUID serverId) {
    return activeSanctions(sanctionRepository.getSanctions(userId, serverId));
  }

  @Transactional
  public boolean isAppBanned() {
    return sanctionRepository.getSanctions(userHolder.getId())
                             .anyMatch(sanction -> sanction.getType().equals(BAN) && sanction.isActive());
  }

  @Transactional
  public boolean isSanctioned(UUID userId, UUID serverId, SanctionType sanctionType) {
    return sanctionRepository.getSanctions(userId, serverId)
                             .filter(Sanction::isActive)
                             .map(Sanction::getType)
                             .anyMatch(List.of(BAN, sanctionType)::contains);
  }

  private List<Sanction> activeSanctions(Stream<Sanction> sanctions) {
    return sanctions.filter(Sanction::isActive).toList();
  }
}
