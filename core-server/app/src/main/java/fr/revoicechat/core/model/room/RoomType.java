package fr.revoicechat.core.model.room;

public enum RoomType {
  TEXT(true, false),
  WEBRTC(false, false),
  VOICE(false, true),
  PRIVATE_MESSAGE(true, true),
  ;

  private final boolean textual;
  private final boolean vocal;

  RoomType(final boolean textual, final boolean vocal) {
    this.textual = textual;
    this.vocal = vocal;
  }

  public boolean isTextual() {
    return textual;
  }

  public boolean isVocal() {
    return vocal;
  }
}
