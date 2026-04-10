package fr.revoicechat.core.technicaldata.user;

import java.util.Collection;

import fr.revoicechat.core.model.User;

public record NewUser(User user, Collection<String> recoverCodes) {}
