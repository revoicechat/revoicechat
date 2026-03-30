package fr.revoicechat.core.repository.jpa;

import static org.assertj.core.api.Assertions.*;

import java.io.IOError;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.revoicechat.core.model.MessageReactions;
import fr.revoicechat.core.model.MessageReactions.MessageReaction;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestMessageReactionsConverter {

  private static final String JSON = """
      {
        "reactions" : [ {
          "emoji" : "😀",
          "users" : [ "cafcb646-dc66-4376-bc81-2a9e2ea9fbe6" ]
        }, {
          "emoji" : "👽",
          "users" : [ "b22e3986-3015-4fd8-bb00-183131850ab2" ]
        }, {
          "emoji" : "🍟",
          "users" : [ "cafcb646-dc66-4376-bc81-2a9e2ea9fbe6", "b22e3986-3015-4fd8-bb00-183131850ab2" ]
        } ]
      }""";

  @Test
  void testConvertToDatabaseColumn() {
    var reactions = getReactions();
    var mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    var converter = new MessageReactionsConverter(mapper);
    assertThat(converter.convertToDatabaseColumn(reactions)).isEqualToNormalizingNewlines(JSON);
  }

  @Test
  void testConvertToDatabaseColumnWithError() {
    var reactions = getReactions();
    var mapper = new ObjectMapper() {
      @Override
      public String writeValueAsString(final Object value) throws JsonProcessingException {
        throw new JsonProcessingException("") {};
      }
    };
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    var converter = new MessageReactionsConverter(mapper);
    assertThatThrownBy(() -> converter.convertToDatabaseColumn(reactions)).isInstanceOf(IOError.class);
  }

  @Test
  void testConvertToEntityAttribute() {
    var converter = new MessageReactionsConverter();
    var structure = converter.convertToEntityAttribute(JSON);
    assertThat(structure).usingRecursiveAssertion().isEqualTo(getReactions());
  }

  @Test
  void testConvertToEntityAttributeNull() {
    var converter = new MessageReactionsConverter();
    var structure = converter.convertToEntityAttribute(null);
    assertThat(structure).usingRecursiveAssertion().isEqualTo(new MessageReactions(List.of()));
  }

  @Test
  void testConvertToEntityAttributeEmpty() {
    var converter = new MessageReactionsConverter();
    var structure = converter.convertToEntityAttribute("");
    assertThat(structure).usingRecursiveAssertion().isEqualTo(new MessageReactions(List.of()));
  }

  @Test
  void testConvertToEntityAttributeWithError() {
    var mapper = new ObjectMapper() {
      @Override
      public <T> T readValue(String content, Class<T> classes) throws JsonProcessingException {
        throw new JsonProcessingException("") {};
      }
    };
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    var converter = new MessageReactionsConverter(mapper);
    assertThatThrownBy(() -> converter.convertToEntityAttribute(JSON)).isInstanceOf(IOError.class);
  }

  private MessageReactions getReactions() {
    return new MessageReactions(List.of(
        new MessageReaction("😀", List.of(UUID.fromString("cafcb646-dc66-4376-bc81-2a9e2ea9fbe6"))),
        new MessageReaction("👽", List.of(UUID.fromString("b22e3986-3015-4fd8-bb00-183131850ab2"))),
        new MessageReaction("🍟", List.of(UUID.fromString("cafcb646-dc66-4376-bc81-2a9e2ea9fbe6"), UUID.fromString("b22e3986-3015-4fd8-bb00-183131850ab2")))
    ));
  }
}