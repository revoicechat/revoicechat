package fr.revoicechat.openapi.web;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;

import fr.revoicechat.openapi.SchemaFilterer;

public class EnumFormatFilter implements SchemaFilterer {

  @Override
  public Schema filterSchema(Schema schema) {
    if (isStringType(schema) && isEnum(schema)) {
      schema.setFormat("enum");
      schema.setPattern(schema.getEnumeration().stream().map(Object::toString).collect(Collectors.joining("|", "(", ")")));
    }
    return schema;
  }

  private static boolean isStringType(final Schema schema) {
    return schema.getType() != null && schema.getType().contains(SchemaType.STRING);
  }

  private static boolean isEnum(final Schema schema) {
    return Optional.ofNullable(schema.getEnumeration())
                   .filter(Predicate.not(List::isEmpty))
                   .isPresent();
  }
}
