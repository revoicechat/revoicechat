package fr.revoicechat.core.model;

import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestMessageReactions {

  @Test
  void testAddReaction() {
    var noReactions = new MessageReactions(List.of());
    var result = noReactions.toggle("👽", UUID.randomUUID());
    Assertions.assertThat(result.reactions()).hasSize(1);
    Assertions.assertThat(result.reactions().getFirst().emoji()).isEqualTo("👽");
    Assertions.assertThat(result.reactions().getFirst().users()).hasSize(1);
  }

  @Test
  void testAddSameReaction() {
    var noReactions = new MessageReactions(List.of());
    var result = noReactions.toggle("👽", UUID.randomUUID())
                            .toggle("👽", UUID.randomUUID())
                            .toggle("😀", UUID.randomUUID());
    Assertions.assertThat(result.reactions()).hasSize(2);
    var reaction1 = result.reactions().getFirst();
    Assertions.assertThat(reaction1.emoji()).isEqualTo("👽");
    Assertions.assertThat(reaction1.users()).hasSize(2);
    var reaction2 = result.reactions().getLast();
    Assertions.assertThat(reaction2.emoji()).isEqualTo("😀");
    Assertions.assertThat(reaction2.users()).hasSize(1);
  }

  @Test
  void testRemoveAllReaction() {
    var noReactions = new MessageReactions(List.of());
    var user1 = UUID.randomUUID();
    var result = noReactions.toggle("👽", user1).toggle("👽", user1);
    Assertions.assertThat(result.reactions()).isEmpty();
  }

  @Test
  void testRemoveReaction() {
    var noReactions = new MessageReactions(List.of());
    var user1 = UUID.randomUUID();
    var result = noReactions.toggle("👽", user1)
                            .toggle("👽", UUID.randomUUID())
                            .toggle("👽", user1);
    Assertions.assertThat(result.reactions()).hasSize(1);
    var reaction1 = result.reactions().getFirst();
    Assertions.assertThat(reaction1.emoji()).isEqualTo("👽");
    Assertions.assertThat(reaction1.users()).hasSize(1).doesNotContain(user1);
  }
}