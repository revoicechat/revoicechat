package fr.revoicechat.notification.web;

import static org.eclipse.microprofile.openapi.OASFactory.createSchema;
import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;

import fr.revoicechat.notification.model.NotificationPayload;
import fr.revoicechat.notification.model.NotificationType;
import fr.revoicechat.openapi.OpenAPIFilterer;

public class NotificationOpenAPIFilterer implements OpenAPIFilterer {

  @Override
  public void filterOpenAPI(OpenAPI openAPI) {
    var pathItem = openAPI.getPaths().getPathItem("/api/sse");
    Set<Class<? extends NotificationPayload>> payloads = PayloadClassesHolder.INSTANCE.getPayloads();
    pathItem.getGET()
            .getResponses()
            .getAPIResponse("200")
            .getContent()
            .getMediaType("application/json")
            .setSchema(createSchema().type(List.of(SchemaType.OBJECT))
                                     .addProperty("type", notificationTypes(payloads))
                                     .addProperty("data", notificationData(payloads)));
  }

  private Schema notificationTypes(final Set<Class<? extends NotificationPayload>> payloads) {
    List<Object> enumeration = payloads.stream()
                                       .map(clazz -> clazz.getAnnotation(NotificationType.class))
                                       .filter(Objects::nonNull)
                                       .map(NotificationType::name)
                                       .collect(Collectors.toUnmodifiableList());
    return createSchema().format("enum")
                         .enumeration(enumeration)
                         .pattern(enumeration.stream().map(Object::toString).collect(joining("|", "(", ")")));
  }

  private Schema notificationData(final Set<Class<? extends NotificationPayload>> payloads) {
    return createSchema().oneOf(payloads.stream()
                                        .map(c -> createSchema().ref("#/components/schemas/" + c.getSimpleName()))
                                        .toList());
  }
}
