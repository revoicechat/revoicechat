package fr.revoicechat.web.mapper.error;

import static fr.revoicechat.web.nls.HttpStatusErrorCode.*;

import java.io.IOError;
import java.nio.charset.StandardCharsets;
import java.util.List;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import fr.revoicechat.i18n.LocalizedMessage;

final class ErrorMapperUtils {
  private ErrorMapperUtils() {}

  static final String JSON_MESSAGE = fetchForbiddenAccessFile("/static/generic-error-template.json");
  static final String HTML_MESSAGE = fetchForbiddenAccessFile("/static/generic-error-template.html");

  static final String UNKNOWN_JSON_MESSAGE = fetchForbiddenAccessFile("/static/unknown-error-template.json");
  static final String UNKNOWN_HTML_MESSAGE = fetchForbiddenAccessFile("/static/unknown-error-template.html");

  @SuppressWarnings("java:S1181") // generate an IOError for any throwable
  static String fetchForbiddenAccessFile(String name) {
    try (var ressource = ErrorMapperUtils.class.getResourceAsStream(name)) {
      assert ressource != null;
      return new String(ressource.readAllBytes(), StandardCharsets.UTF_8);
    } catch (Throwable e) {
      throw new IOError(e);
    }
  }

  static ResourceMediaType determineResponseType(HttpHeaders headers) {
    if (headers != null) {
      List<MediaType> acceptable = headers.getAcceptableMediaTypes();
      for (MediaType mediaType : acceptable) {
        if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
          return ResourceMediaType.APPLICATION_JSON;
        } else if (mediaType.isCompatible(MediaType.TEXT_HTML_TYPE)) {
          return ResourceMediaType.TEXT_HTML;
        }
      }
    }
    return ResourceMediaType.TEXT_HTML;
  }

  enum ResourceMediaType {
    APPLICATION_JSON(MediaType.APPLICATION_JSON_TYPE, JSON_MESSAGE, UNKNOWN_JSON_MESSAGE),
    TEXT_HTML(MediaType.TEXT_HTML_TYPE, HTML_MESSAGE, UNKNOWN_HTML_MESSAGE),;

    private final MediaType type;
    private final String genericErrorFile;
    private final String unknownErrorFile;

    ResourceMediaType(MediaType type, String genericErrorFile, String unknownErrorFile) {
      this.type = type;
      this.genericErrorFile = genericErrorFile;
      this.unknownErrorFile = unknownErrorFile;
    }

    public MediaType type() {
      return type;
    }

    public String genericErrorFile(LocalizedMessage title, LocalizedMessage message) {
      return genericErrorFile.formatted(title.translate(), message.translate(), DOCUMENTATION_API_LINK.translate());
    }

    public String unknownErrorFile(final String fileName) {
      return unknownErrorFile.formatted(
          INTERNAL_SERVER_ERROR_TITLE.translate(),
          INTERNAL_SERVER_ERROR_MESSAGE.translate(),
          fileName,
          DOCUMENTATION_API_LINK.translate());
    }
  }
}
