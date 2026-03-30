package fr.revoicechat.core.technicaldata.message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import fr.revoicechat.core.technicaldata.media.NewMediaData;

public record NewMessage(
    String text,
    UUID answerTo,
    List<NewMediaData> medias) {

  public NewMessage(
      String text,
      UUID answerTo,
      List<NewMediaData> medias) {
    this.text = Optional.ofNullable(text).map(String::trim).orElse("");
    this.answerTo = answerTo;
    this.medias = Optional.ofNullable(medias).orElse(List.of());
  }
}
