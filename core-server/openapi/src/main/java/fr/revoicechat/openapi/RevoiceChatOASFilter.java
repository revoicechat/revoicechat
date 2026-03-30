package fr.revoicechat.openapi;

import java.util.List;
import java.util.ServiceLoader;

import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.media.Schema;

import io.quarkus.smallrye.openapi.OpenApiFilter;

@OpenApiFilter(OpenApiFilter.RunStage.BUILD)
public class RevoiceChatOASFilter implements OASFilter {

  public final List<SchemaFilterer> schemaFilterers;
  public final List<OpenAPIFilterer> openAPIFilterers;

  public RevoiceChatOASFilter() {
    this(ServiceLoader.load(SchemaFilterer.class).stream()
                      .map(ServiceLoader.Provider::get)
                      .toList(),
        ServiceLoader.load(OpenAPIFilterer.class).stream()
                     .map(ServiceLoader.Provider::get)
                     .toList());
  }

  public RevoiceChatOASFilter(final List<SchemaFilterer> schemaFilterers, final List<OpenAPIFilterer> openAPIFilterers) {
    this.schemaFilterers = schemaFilterers;
    this.openAPIFilterers = openAPIFilterers;
  }

  @Override
  public Schema filterSchema(Schema schema) {
    for (SchemaFilterer filterer : schemaFilterers) {
      schema = filterer.filterSchema(schema);
    }
    return schema;
  }

  @Override
  public void filterOpenAPI(final OpenAPI openAPI) {
    openAPIFilterers.forEach(openAPIFilterer -> openAPIFilterer.filterOpenAPI(openAPI));
  }
}
