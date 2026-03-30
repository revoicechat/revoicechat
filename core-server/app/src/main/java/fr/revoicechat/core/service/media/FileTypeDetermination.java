package fr.revoicechat.core.service.media;

import java.util.List;
import java.util.stream.Stream;

import fr.revoicechat.core.model.FileType;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
class FileTypeDetermination {

  FileType get(String fileName) {
    if (fileName == null) {
      return FileType.OTHER;
    }
    var file = fileName.split("\\.");
    var extension = file[file.length - 1];
    return Extension.of(extension);
  }

  private enum Extension {
    // === Images ===
    PICTURE(FileType.PICTURE, List.of("jpg", "jpeg", "png", "gif", "webp", "avif", "apng", "ico", "bmp", "tiff", "tif", "heic")),
    SVG(FileType.SVG, List.of("svg")),
    // === Videos ===
    VIDEO(FileType.VIDEO, List.of("mp4", "webm", "ogv", "avi", "mkv", "mov", "flv", "wmv", "mpeg", "mpg")),
    // === Audio ===
    AUDIO(FileType.AUDIO, List.of("mp3", "wav", "ogg", "flac", "aac", "m4a", "wma", "opus")),
    // === Documents ===
    PDF(FileType.PDF, List.of("pdf")),
    TEXT(FileType.TEXT, List.of("txt", "log", "ini", "cfg", "md", "csv", "json", "yaml", "yml", "xml", "toml")),
    OFFICE(FileType.OFFICE, List.of("doc", "docx", "xls", "xlsx", "ppt", "pptx", "odt", "ods", "odp", "rtf")),
    // === Archives & Packages ===
    ARCHIVE(FileType.ARCHIVE, List.of("zip", "rar", "7z", "tar", "gz", "bz2", "xz", "iso")),
    // === Code & Scripts ===
    CODE(FileType.CODE, List.of(
        // Web
        "html", "htm", "css", "js", "ts", "tsx", "jsx",
        // Backend / general
        "java", "kt", "kts", "scala", "groovy", "c", "cpp", "h", "hpp", "cs", "go", "rs", "swift", "dart",
        // Scripts
        "py", "rb", "php", "sh", "bat", "ps1",
        // Config / templates
        "sql", "jsp", "vue", "svelte"
    )),
    // === Fonts ===
    FONT(FileType.FONT, List.of("ttf", "otf", "woff", "woff2", "eot")),
    // === 3D / CAD ===
    MODEL(FileType.MODEL, List.of("obj", "fbx", "stl", "gltf", "glb", "blend", "3ds", "dae")),
    // === Executables ===
    EXECUTABLE(FileType.EXECUTABLE, List.of("exe", "msi", "app", "apk", "jar", "bat", "sh", "bin")),
    // === Data / Database files ===
    DATA(FileType.DATA, List.of("sql", "db", "sqlite", "mdb", "accdb", "xml", "json", "csv", "parquet", "avro")),
    ;

    private final FileType fileType;
    private final List<String> extensions;

    Extension(final FileType fileType, final List<String> extensions) {
      this.fileType = fileType;
      this.extensions = extensions;
    }

    public static FileType of(final String extensionFile) {
      return Stream.of(values())
                   .filter(extension -> extension.extensions.contains(extensionFile.toLowerCase()))
                   .map(extension -> extension.fileType)
                   .findFirst()
                   .orElse(FileType.OTHER);
    }
  }
}
