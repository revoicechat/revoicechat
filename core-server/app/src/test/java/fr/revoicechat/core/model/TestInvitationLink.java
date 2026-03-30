package fr.revoicechat.core.model;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestInvitationLink {

  @Test
  @SuppressWarnings({ "java:S5838", "java:S5863", "EqualsWithItself" })
  void test() {
    var id1 = UUID.randomUUID();
    var invitationLink1 = new InvitationLink();
    invitationLink1.setId(id1);
    var invitationLink2 = new InvitationLink();
    invitationLink2.setId(id1);
    var invitationLink3 = new InvitationLink();
    invitationLink3.setId(UUID.randomUUID());

    assertThat(invitationLink1).isEqualTo(invitationLink1)
                     .isEqualTo(invitationLink2)
                     .hasSameHashCodeAs(invitationLink2)
                     .isNotEqualTo(invitationLink3)
                     .isNotEqualTo(null)
                     .isNotEqualTo(new Object());
  }
}