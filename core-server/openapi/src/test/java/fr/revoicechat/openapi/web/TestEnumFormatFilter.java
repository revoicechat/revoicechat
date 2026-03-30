package fr.revoicechat.openapi.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.*;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestEnumFormatFilter {

  public static Stream<Arguments> parameters() {
    return Stream.of(
        Arguments.of(new ParameterData(null, null)),
        Arguments.of(new ParameterData(null, List.of())),
        Arguments.of(new ParameterData(null, List.of("VALUE_1"))),
        Arguments.of(new ParameterData(null, List.of("VALUE_1", "VALUE_2"))),
        Arguments.of(new ParameterData(OBJECT, null)),
        Arguments.of(new ParameterData(OBJECT, List.of())),
        Arguments.of(new ParameterData(OBJECT, List.of("VALUE_1"))),
        Arguments.of(new ParameterData(OBJECT, List.of("VALUE_1", "VALUE_2"))),
        Arguments.of(new ParameterData(STRING, null)),
        Arguments.of(new ParameterData(STRING, List.of())),
        Arguments.of(new ParameterData(STRING, List.of("VALUE_1"), "enum", "(VALUE_1)")),
        Arguments.of(new ParameterData(STRING, List.of("VALUE_1", "VALUE_2"), "enum", "(VALUE_1|VALUE_2)"))
    );
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void test(ParameterData data) {
    var result = new EnumFormatFilter().filterSchema(data.schema());
    assertThat(result).isNotNull();
    assertThat(result.getFormat()).isEqualTo(data.format);
    assertThat(result.getPattern()).isEqualTo(data.pattern);
  }

  private record ParameterData(SchemaType type, List<Object> enumeration, String format, String pattern) {

    public ParameterData(final SchemaType type, final List<Object> enumeration) {
      this(type, enumeration, null, null);
    }

    Schema schema() {
      return type == null
             ? OASFactory.createSchema().type(null).enumeration(enumeration)
             : OASFactory.createSchema().type(List.of(type)).enumeration(enumeration);
    }
  }
}