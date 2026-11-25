package fr.revoicechat.core.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.revoicechat.core.model.MessageReactions;
import fr.revoicechat.core.model.MessageReactions.MessageReaction;
import io.quarkus.test.junit.QuarkusTest;
import tools.jackson.databind.json.JsonMapper;

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
    var mapper = JsonMapper.builder().enable(tools.jackson.databind.SerializationFeature.INDENT_OUTPUT).build();
    var converter = new MessageReactionsConverter(mapper);
    assertThat(converter.convertToDatabaseColumn(reactions)).isEqualToNormalizingNewlines(JSON);
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

  private MessageReactions getReactions() {
    return new MessageReactions(List.of(
        new MessageReaction("😀", List.of(UUID.fromString("cafcb646-dc66-4376-bc81-2a9e2ea9fbe6"))),
        new MessageReaction("👽", List.of(UUID.fromString("b22e3986-3015-4fd8-bb00-183131850ab2"))),
        new MessageReaction("🍟", List.of(UUID.fromString("cafcb646-dc66-4376-bc81-2a9e2ea9fbe6"), UUID.fromString("b22e3986-3015-4fd8-bb00-183131850ab2")))
    ));
  }
}