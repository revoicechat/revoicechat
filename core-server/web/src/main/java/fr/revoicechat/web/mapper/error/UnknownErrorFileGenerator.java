package fr.revoicechat.web.mapper.error;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UnknownErrorFileGenerator {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

  @ConfigProperty(name = "revoicechat.dev.error.log.directory.path", defaultValue = "log/errors")
  String errorLogDirectoryPath;

  public String generate(Throwable exception) {
    String timestamp = LocalDateTime.now().format(FORMATTER);
    String uuid = UUID.randomUUID().toString();
    String fileName = timestamp + "-ERR-" + uuid + ".log";
    Path logDir = Paths.get(errorLogDirectoryPath);
    Path logFile = logDir.resolve(fileName);
    try (var printer = new PrintStream(logFile.toFile())) {
      Files.createDirectories(logDir);
      exception.printStackTrace(printer);
    } catch (IOException _) {
      fileName = "unable to generate an internal error file";
    }
    return fileName;
  }
}
