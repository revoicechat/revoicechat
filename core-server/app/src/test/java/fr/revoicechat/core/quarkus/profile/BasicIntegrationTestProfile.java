package fr.revoicechat.core.quarkus.profile;

import static java.util.Map.entry;

import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class BasicIntegrationTestProfile implements QuarkusTestProfile {
  @Override
  public Map<String, String> getConfigOverrides() {
    return Map.ofEntries(
        // invitation not required
        entry("revoicechat.global.app-only-accessible-by-invitation", "false"),
        // database in H2 for offline testing
        entry("quarkus.datasource.db-kind", "h2"),
        entry("quarkus.datasource.jdbc.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"),
        entry("quarkus.datasource.username", "sa"),
        entry("quarkus.datasource.password", "sa")
    );
  }
}
