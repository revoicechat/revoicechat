package fr.revoicechat.security.repository;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.security.model.UserRecoverCode;

public interface UserRecoverCodeRepository {
  List<UserRecoverCode> findByUser(UUID userId);
}
