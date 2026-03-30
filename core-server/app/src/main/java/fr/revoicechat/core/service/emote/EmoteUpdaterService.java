package fr.revoicechat.core.service.emote;

import static fr.revoicechat.risk.nls.RiskMembershipErrorCode.RISK_MEMBERSHIP_ERROR;

import java.util.UUID;

import fr.revoicechat.core.model.Emote;
import fr.revoicechat.core.model.FileType;
import fr.revoicechat.core.model.MediaOrigin;
import fr.revoicechat.core.nls.EmoteErrorCode;
import fr.revoicechat.core.risk.EmoteRiskType;
import fr.revoicechat.core.service.emote.risk.EmoteRiskSupplier;
import fr.revoicechat.core.service.media.MediaDataService;
import fr.revoicechat.core.technicaldata.emote.NewEmote;
import fr.revoicechat.core.technicaldata.media.NewMediaData;
import fr.revoicechat.risk.type.RiskType;
import fr.revoicechat.security.UserHolder;
import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.web.error.BadRequestException;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EmoteUpdaterService {

  private final MediaDataService mediaDataService;
  private final EntityManager entityManager;
  private final UserHolder userHolder;
  private final EmoteRetrieverService emoteRetrieverService;
  private final Instance<EmoteRiskSupplier> emoteRiskSuppliers;

  @Inject
  public EmoteUpdaterService(MediaDataService mediaDataService,
                             EntityManager entityManager,
                             UserHolder userHolder,
                             EmoteRetrieverService emoteRetrieverService,
                             Instance<EmoteRiskSupplier> emoteRiskSuppliers) {
    this.mediaDataService = mediaDataService;
    this.entityManager = entityManager;
    this.userHolder = userHolder;
    this.emoteRetrieverService = emoteRetrieverService;
    this.emoteRiskSuppliers = emoteRiskSuppliers;
  }

  @Transactional
  public Emote add(final UUID id, final NewEmote newEmote) {
    var media = mediaDataService.create(new NewMediaData(newEmote.fileName()), MediaOrigin.EMOTE);
    if (!media.getType().equals(FileType.PICTURE)) {
      throw new BadRequestException(EmoteErrorCode.ONLY_PICTURES_ERR);
    }
    Emote emote = new Emote();
    emote.setId(media.getId());
    emote.setContent(newEmote.content());
    emote.setKeywords(newEmote.keywords());
    emote.setEntity(id);
    emote.setMedia(media);
    entityManager.persist(emote);
    return emote;
  }

  @Transactional
  public Emote update(final UUID id, final NewEmote newEmote) {
    var user = userHolder.get();
    var emote = emoteRetrieverService.getEntity(id);
    if (hasRisk(emote, user, EmoteRiskType.UPDATE_EMOTE)) {
      emote.setContent(newEmote.content());
      emote.setKeywords(newEmote.keywords());
      entityManager.persist(emote);
      return emote;
    } else {
      throw new UnauthorizedException(RISK_MEMBERSHIP_ERROR.translate(EmoteRiskType.UPDATE_EMOTE));
    }
  }

  @Transactional
  public Emote delete(final UUID id) {
    var user = userHolder.get();
    var emote = emoteRetrieverService.getEntity(id);
    if (hasRisk(emote, user, EmoteRiskType.REMOVE_EMOTE)) {
      entityManager.remove(emote);
      return emote;
    } else {
      throw new UnauthorizedException(RISK_MEMBERSHIP_ERROR.translate(EmoteRiskType.REMOVE_EMOTE));
    }
  }

  private boolean hasRisk(final Emote emote, final AuthenticatedUser user, RiskType riskType) {
    return emoteRiskSuppliers.stream().anyMatch(emoteRiskSupplier -> emoteRiskSupplier.hasRisk(emote, user, riskType));
  }
}
