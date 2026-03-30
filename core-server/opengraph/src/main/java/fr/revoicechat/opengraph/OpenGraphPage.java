package fr.revoicechat.opengraph;

public record OpenGraphPage(String pageUrl,
                            String description,
                            String siteName,
                            String locale,
                            String localeAlternate) {}
