package fr.revoicechat.risk.technicaldata;

import java.util.Comparator;
import java.util.UUID;

import org.jspecify.annotations.NonNull;

import fr.revoicechat.risk.model.RiskMode;

public record AffectedRisk(UUID role,
                           RiskMode mode,
                           UUID entity,
                           int priority) implements Comparable<AffectedRisk> {

  @Override
  public int compareTo(@NonNull AffectedRisk that) {
    return Comparator.comparing(AffectedRisk::priority)
                     .thenComparing(AffectedRisk::compareByEntity)
                     .compare(this, that);
  }

  private static int compareByEntity(final AffectedRisk r1, final AffectedRisk r2) {
    if (r1.entity == r2.entity) {
      return 0;
    } else if (r1.entity == null) {
      return 1;
    } else if (r2.entity == null) {
      return -1;
    } else {
      return r1.entity.compareTo(r2.entity);
    }
  }
}