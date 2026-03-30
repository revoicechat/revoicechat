package fr.revoicechat.opengraph.service;

import java.io.IOException;

import org.jsoup.nodes.Document;

@FunctionalInterface
interface HttpFetcher {
  Document fetch(String url) throws IOException, InterruptedException;
}
