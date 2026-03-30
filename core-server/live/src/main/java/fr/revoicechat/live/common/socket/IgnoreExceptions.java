package fr.revoicechat.live.common.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class IgnoreExceptions {
  private static final Logger LOG = LoggerFactory.getLogger(IgnoreExceptions.class);

  private IgnoreExceptions() {/*not instantiable*/}

  public static void run(ExceptionRunner runner) {
    try {
      runner.run();
    } catch (Exception e) {
      LOG.warn(e.getMessage());
    }
  }

  @FunctionalInterface
  public interface ExceptionRunner {
    @SuppressWarnings("java:S112") // the goal here is to ignore any exception
    void run() throws Exception;
  }
}
