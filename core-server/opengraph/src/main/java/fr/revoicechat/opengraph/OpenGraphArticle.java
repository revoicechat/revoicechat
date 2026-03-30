package fr.revoicechat.opengraph;

import java.util.List;

public record OpenGraphArticle(String publishedTime,
                               String modifiedTime,
                               String expirationTime,
                               String author,
                               String section,
                               List<String> tags) {}
