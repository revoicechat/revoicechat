package fr.revoicechat.security.representation;

import java.util.Arrays;

public record TotpQRCode(String url, byte[] pgn) {
  @Override
  public boolean equals(final Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final TotpQRCode that = (TotpQRCode) o;
    return url.equals(that.url) && Arrays.equals(pgn, that.pgn);
  }

  @Override
  public int hashCode() {
    return 31 * url.hashCode() + Arrays.hashCode(pgn);
  }

  @Override
  public String toString() {
    return "TotpQRCode{url='%s'}".formatted(url);
  }
}
