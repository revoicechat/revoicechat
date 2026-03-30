package fr.revoicechat.core.service.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SettingsService {

  public Map<String, Object> get() {
    Config config = ConfigProvider.getConfig();
    Map<String, Object> props = new HashMap<>();
    StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                 .filter(name -> name.startsWith("revoicechat."))
                 .filter(name -> !name.contains(".dev."))
                 .filter(name -> !name.contains(".test."))
                 .forEach(name -> config.getOptionalValue(name, String.class)
                                        .ifPresent(value -> props.put(name.replace("revoicechat.", ""), value)));
    return props;
  }
}
