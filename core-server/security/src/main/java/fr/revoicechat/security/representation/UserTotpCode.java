package fr.revoicechat.security.representation;

public record UserTotpCode(String username, String code) {}
