package fr.revoicechat.core.service.settings;

import static org.assertj.core.api.Assertions.*;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(BasicIntegrationTestProfile.class)
class TestSettingsService {

  @Inject SettingsService settingsService;

  @Test
  void test() {
    var settings = settingsService.get();
    assertThat(settings).containsKeys(
        "global.app-only-accessible-by-invitation",
        "message.max-length"
    ).doesNotContainKeys("dev.error.log");
  }
}