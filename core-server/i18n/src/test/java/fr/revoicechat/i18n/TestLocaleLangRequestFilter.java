package fr.revoicechat.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URISyntaxException;
import java.util.Locale;

import org.jboss.resteasy.core.interception.jaxrs.ResponseContainerRequestContext;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestLocaleLangRequestFilter {

  @Test
  void test() throws URISyntaxException {
    var request = MockHttpRequest.get("/test");
    request.header("Accept-Language", "fr");
    var context = new ResponseContainerRequestContext(request);
    assertThat(CurrentLocaleHolder.getLocale()).hasSize(1).containsExactly(Locale.ENGLISH);
    new LocaleLangRequestFilter().filter(context);
    assertThat(CurrentLocaleHolder.getLocale()).hasSize(2).containsExactly(Locale.FRENCH, Locale.ENGLISH);
    new LocaleLangRequestFilter().filter(context, null);
    assertThat(CurrentLocaleHolder.getLocale()).hasSize(1).containsExactly(Locale.ENGLISH);
  }
}