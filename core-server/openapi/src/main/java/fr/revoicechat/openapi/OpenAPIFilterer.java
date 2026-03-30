package fr.revoicechat.openapi;

import org.eclipse.microprofile.openapi.models.OpenAPI;

public interface OpenAPIFilterer {
  void filterOpenAPI(final OpenAPI openAPI);
}
