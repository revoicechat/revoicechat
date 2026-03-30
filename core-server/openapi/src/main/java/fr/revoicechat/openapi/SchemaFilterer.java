package fr.revoicechat.openapi;

import org.eclipse.microprofile.openapi.models.media.Schema;

public interface SchemaFilterer {

  Schema filterSchema(final Schema schema);
}
