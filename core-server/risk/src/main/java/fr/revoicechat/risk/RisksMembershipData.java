package fr.revoicechat.risk;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import jakarta.interceptor.InterceptorBinding;

import fr.revoicechat.moderation.model.SanctionType;

@InterceptorBinding
@Retention(RUNTIME)
@Target({ METHOD, TYPE })
@RisksMembership
@Inherited
public @interface RisksMembershipData {

  String[] risks();

  Class<? extends RisksEntityRetriever> retriever();

  SanctionType sanctionType() default SanctionType.BAN;
}
