package fr.revoicechat.core.technicaldata.user;

import fr.revoicechat.core.model.UserType;

public record AdminUpdatableUserData(String displayName, UserType type) {}
