package fr.revoicechat.security.representation;

public record TotpQRCode(String url, byte[] pgn) {}
