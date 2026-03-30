package fr.revoicechat.security.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestPasswordUtils {

  @Test
  void testEncode() {
    assertThat(PasswordUtils.encodePassword("psw")).isNotEqualTo("psw");
  }

  @Test
  void testMatch() {
    var encoded = PasswordUtils.encodePassword("psw");
    assertThat(PasswordUtils.matches("psw", encoded)).isTrue();
    assertThat(PasswordUtils.matches("pSw", encoded)).isFalse();
    assertThat(PasswordUtils.matches("notSame", encoded)).isFalse();
  }
}