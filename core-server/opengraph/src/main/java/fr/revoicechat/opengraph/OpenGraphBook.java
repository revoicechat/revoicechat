package fr.revoicechat.opengraph;

import java.util.List;

public record OpenGraphBook(String author,
                            String isbn,
                            String releaseDate,
                            List<String> tags) {}
