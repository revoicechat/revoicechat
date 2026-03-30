package fr.revoicechat.risk.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.revoicechat.i18n.LocalizedMessage;
import fr.revoicechat.i18n.RiskLocalizedMessage;
import fr.revoicechat.risk.service.risk.RiskTypeDeserializer;
import fr.revoicechat.risk.service.risk.RiskTypeSerializer;

/**
 * Define the risk type.
 * All hardcoded implementation are here to provide the list of risks and its translation.
 */
@JsonDeserialize(using = RiskTypeDeserializer.class)
@JsonSerialize(using = RiskTypeSerializer.class)
public interface RiskType extends RiskLocalizedMessage {}
