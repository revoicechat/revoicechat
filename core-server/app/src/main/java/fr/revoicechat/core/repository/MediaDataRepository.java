package fr.revoicechat.core.repository;

import java.util.stream.Stream;

import fr.revoicechat.core.model.MediaData;
import fr.revoicechat.core.model.MediaDataStatus;

public interface MediaDataRepository {
  Stream<MediaData> findByStatus(MediaDataStatus status);
}
