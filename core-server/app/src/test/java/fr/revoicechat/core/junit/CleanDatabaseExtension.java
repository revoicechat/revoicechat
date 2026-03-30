package fr.revoicechat.core.junit;

import jakarta.enterprise.inject.spi.CDI;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

class CleanDatabaseExtension implements BeforeEachCallback {

  @Override
  public void beforeEach(final ExtensionContext context) {
    CDI.current().select(DBCleaner.class).get().clean();
  }
}
