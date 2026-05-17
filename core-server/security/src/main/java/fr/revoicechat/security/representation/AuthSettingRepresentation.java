package fr.revoicechat.security.representation;

public record AuthSettingRepresentation(boolean totpActive,
                                        long remainingRecoveryCode) {}
