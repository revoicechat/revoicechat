package fr.revoicechat.core.web;

import fr.revoicechat.core.representation.NewUserRepresentation;
import fr.revoicechat.core.service.user.UserService;
import fr.revoicechat.core.technicaldata.user.NewUserSignup;
import fr.revoicechat.core.web.api.AuthController;
import fr.revoicechat.web.mapper.Mapper;
import jakarta.annotation.security.PermitAll;

public class AuthControllerImpl implements AuthController {

  private final UserService userService;

  public AuthControllerImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
  @PermitAll
  public NewUserRepresentation signup(NewUserSignup user) {
    return Mapper.map(userService.create(user));
  }
}
