package fr.revoicechat.core.technicaldata.user;

import java.util.UUID;

public record NewUserSignup(
    String username,
    String password,
    String email,
    UUID invitationLink
) {}
