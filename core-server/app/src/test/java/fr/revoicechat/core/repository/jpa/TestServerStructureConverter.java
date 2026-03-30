package fr.revoicechat.core.repository.jpa;

import static org.assertj.core.api.Assertions.*;

import java.io.IOError;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.revoicechat.core.model.server.ServerCategory;
import fr.revoicechat.core.model.server.ServerRoomItem;
import fr.revoicechat.core.model.server.ServerStructure;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestServerStructureConverter {

  private static final String JSON = """
      {
        "items" : [ {
          "type" : "ROOM",
          "id" : "cafcb646-dc66-4376-bc81-2a9e2ea9fbe6"
        }, {
          "type" : "CATEGORY",
          "name" : "text",
          "items" : [ {
            "type" : "ROOM",
            "id" : "b22e3986-3015-4fd8-bb00-183131850ab2"
          }, {
            "type" : "ROOM",
            "id" : "8bf41da2-0e73-472b-b312-f3967a6a3d6c"
          } ]
        }, {
          "type" : "CATEGORY",
          "name" : "vocal",
          "items" : [ {
            "type" : "CATEGORY",
            "name" : "sub 1",
            "items" : [ {
              "type" : "ROOM",
              "id" : "d37f8f96-64cd-4ae5-8553-024fae4eb99a"
            } ]
          }, {
            "type" : "CATEGORY",
            "name" : "sub 2",
            "items" : [ {
              "type" : "ROOM",
              "id" : "c309e057-66da-4f5b-b451-c35feaafe133"
            }, {
              "type" : "ROOM",
              "id" : "21e053fa-f070-4974-b9de-a2121b8a6421"
            } ]
          } ]
        } ]
      }""";

  @Test
  void testConvertToDatabaseColumn() {
    ServerStructure structure = getStructure();
    var mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    var converter = new ServerStructureConverter(mapper);
    assertThat(converter.convertToDatabaseColumn(structure)).isEqualToNormalizingNewlines(JSON);
  }

  @Test
  void testConvertToDatabaseColumnWithError() {
    ServerStructure structure = getStructure();
    var mapper = new ObjectMapper() {
      @Override
      public String writeValueAsString(final Object value) throws JsonProcessingException {
        throw new JsonProcessingException("") {};
      }
    };
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    var converter = new ServerStructureConverter(mapper);
    assertThatThrownBy(() -> converter.convertToDatabaseColumn(structure)).isInstanceOf(IOError.class);
  }

  @Test
  void testConvertToEntityAttribute() {
    var converter = new ServerStructureConverter();
    var structure = converter.convertToEntityAttribute(JSON);
    assertThat(structure).usingRecursiveAssertion().isEqualTo(getStructure());
  }

  @Test
  void testConvertToEntityAttributeNull() {
    var converter = new ServerStructureConverter();
    var structure = converter.convertToEntityAttribute(null);
    assertThat(structure).usingRecursiveAssertion().isEqualTo(new ServerStructure(List.of()));
  }

  @Test
  void testConvertToEntityAttributeEmpty() {
    var converter = new ServerStructureConverter();
    var structure = converter.convertToEntityAttribute("");
    assertThat(structure).usingRecursiveAssertion().isEqualTo(new ServerStructure(List.of()));
  }

  @Test
  void testConvertToEntityAttributeWithError() {
    var mapper = new ObjectMapper() {
      @Override
      public <T> T readValue(String content, TypeReference<T> valueTypeRef) throws JsonProcessingException {
        throw new JsonProcessingException("") {};
      }
    };
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    var converter = new ServerStructureConverter(mapper);
    assertThatThrownBy(() -> converter.convertToEntityAttribute(JSON)).isInstanceOf(IOError.class);
  }

  private static ServerStructure getStructure() {
    return new ServerStructure(List.of(
        new ServerRoomItem(UUID.fromString("cafcb646-dc66-4376-bc81-2a9e2ea9fbe6")),
        new ServerCategory("text", List.of(
            new ServerRoomItem(UUID.fromString("b22e3986-3015-4fd8-bb00-183131850ab2")),
            new ServerRoomItem(UUID.fromString("8bf41da2-0e73-472b-b312-f3967a6a3d6c"))
        )),
        new ServerCategory("vocal", List.of(
            new ServerCategory("sub 1", List.of(
                new ServerRoomItem(UUID.fromString("d37f8f96-64cd-4ae5-8553-024fae4eb99a"))
            )),
            new ServerCategory("sub 2", List.of(
                new ServerRoomItem(UUID.fromString("c309e057-66da-4f5b-b451-c35feaafe133")),
                new ServerRoomItem(UUID.fromString("21e053fa-f070-4974-b9de-a2121b8a6421"))
            ))
        ))
    ));
  }
}