package fr.revoicechat.opengraph;

/**
 * Find an URL in the text.
 * If there is only one, fetch it, and return it's {@link OpenGraphSchema}.
 */
public interface OpenGraphExtractor {

  /** @return {@code true} if there is only url in the text. */
  boolean hasPreview(String text);

  /**
   * @return the {@link OpenGraphSchema} of the only url in the text,
   *         if there is only one url.
   * If the url does not return an HTML, the {@link OpenGraphSchema} returned must be null.
   */
  OpenGraphSchema extract(String text);
}