package fr.revoicechat.security.repository;

import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.security.representation.NewPassword;
import jakarta.enterprise.context.ApplicationScoped;

public interface AuthenticatedUserRepository {

  AuthenticatedUser findByLogin(String login);
}
