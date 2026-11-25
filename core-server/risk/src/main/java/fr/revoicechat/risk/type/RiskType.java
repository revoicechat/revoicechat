package fr.revoicechat.risk.type;

import fr.revoicechat.i18n.RiskLocalizedMessage;
import fr.revoicechat.risk.service.risk.RiskTypeDeserializer;
import fr.revoicechat.risk.service.risk.RiskTypeSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

/**
 * Define the risk type.
 * All hardcoded implementation are here to provide the list of risks and its translation.
 */
@JsonDeserialize(using = RiskTypeDeserializer.class)
@JsonSerialize(using = RiskTypeSerializer.class)
public interface RiskType extends RiskLocalizedMessage {}
