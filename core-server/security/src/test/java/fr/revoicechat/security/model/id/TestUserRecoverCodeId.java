package fr.revoicechat.security.model.id;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

@QuarkusTest
class TestUserRecoverCodeId {

  @Test
  void testEquals() {
    EqualsVerifier.simple()
                  .forClass(UserRecoverCodeId.class)
                  .suppress(Warning.STRICT_INHERITANCE)
                  .verify();
  }

  @Test
  void testSerializable() throws Exception {
    var id = new UserRecoverCodeId();
    id.setUserId(UUID.randomUUID());
    id.setCode("ABC123");

    var baos = new ByteArrayOutputStream();
    new ObjectOutputStream(baos).writeObject(id);
    var restored = (UserRecoverCodeId) new ObjectInputStream(
        new ByteArrayInputStream(baos.toByteArray())
    ).readObject();

    assertEquals(id, restored);
  }
}