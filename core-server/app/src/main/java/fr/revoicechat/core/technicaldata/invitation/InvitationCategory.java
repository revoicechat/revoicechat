package fr.revoicechat.core.technicaldata.invitation;

import java.util.Optional;

import fr.revoicechat.core.model.InvitationLinkStatus;

public enum InvitationCategory {
  UNIQUE(InvitationLinkStatus.CREATED),
  PERMANENT(InvitationLinkStatus.PERMANENT),
  ;

  private final InvitationLinkStatus initialStatus;

  InvitationCategory(final InvitationLinkStatus initialStatus) {
    this.initialStatus = initialStatus;
  }

  public InvitationLinkStatus getInitialStatus() {
    return initialStatus;
  }

  public static InvitationCategory of(String category) {
    return Optional.ofNullable(category)
                   .map(value -> switch (value.toUpperCase()) {
                     case "UNIQUE" -> UNIQUE;
                     case "PERMANENT" -> PERMANENT;
                     default -> throw new IllegalArgumentException("Invalid category: " + category);
                   })
                   .orElse(UNIQUE);
  }
}
