package fr.revoicechat.core.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "revoicechat.global.password")
public interface UserPasswordConfig {
  int minLength();

  int minUppercase();

  int minLowercase();

  int minNumber();

  int minSpecialChar();
}
