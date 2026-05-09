package fr.revoicechat.security.repository;

import fr.revoicechat.security.model.AuthenticatedUser;

public interface AuthenticatedUserRepository {

  AuthenticatedUser findByLogin(String login);
}
