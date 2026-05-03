package fr.revoicechat.security.representation;

public record UserRecoveryCode(String username, String code) {}