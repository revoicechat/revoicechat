package fr.revoicechat.core.notification.service.profil;

import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_ADMIN;

import java.util.UUID;

import fr.revoicechat.core.model.User;
import fr.revoicechat.core.notification.ProfilPictureUpdate;
import fr.revoicechat.core.repository.UserRepository;
import fr.revoicechat.notification.Notification;
import fr.revoicechat.security.UserHolder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public final class UserPictureUpdater implements PictureUpdater<User> {
  private final UserHolder userHolder;
  private final UserRepository userRepository;
  private final EntityManager entityManager;

  public UserPictureUpdater(UserHolder userHolder, EntityManager entityManager, UserRepository userRepository) {
    this.userHolder = userHolder;
    this.entityManager = entityManager;
    this.userRepository = userRepository;
  }

  @Override
  public User get(final UUID id) {
    return entityManager.find(User.class, id);
  }

  @Override
  public void emmit(final User user) {
    var currentUser = userHolder.get();
    if (currentUser.getId().equals(user.getId()) || currentUser.getRoles().contains(ROLE_ADMIN)) {
      Notification.of(new ProfilPictureUpdate(user.getId()))
                  .sendTo(userRepository.everyone());
    }
  }
}