package fr.revoicechat.web.stub;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;

public abstract class HttpHeadersMock implements HttpHeaders {
  @Override
  public List<String> getRequestHeader(final String name) {
    return List.of();
  }

  @Override
  public String getHeaderString(final String name) {
    return "";
  }

  @Override
  public MultivaluedMap<String, String> getRequestHeaders() {
    return null;
  }

  @Override
  public List<MediaType> getAcceptableMediaTypes() {
    return List.of();
  }

  @Override
  public List<Locale> getAcceptableLanguages() {
    return List.of();
  }

  @Override
  public MediaType getMediaType() {
    return null;
  }

  @Override
  public Locale getLanguage() {
    return null;
  }

  @Override
  public Map<String, Cookie> getCookies() {
    return Map.of();
  }

  @Override
  public Date getDate() {
    return null;
  }

  @Override
  public int getLength() {
    return 0;
  }
}
