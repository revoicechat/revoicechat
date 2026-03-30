package fr.revoicechat.core.service.media;

import static fr.revoicechat.core.model.FileType.OTHER;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import fr.revoicechat.core.model.FileType;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestFileTypeDetermination {

  @ParameterizedTest
  @CsvSource(delimiterString = " -> ", textBlock = """
      file.jpg    -> PICTURE
      file.jpeg   -> PICTURE
      file.png    -> PICTURE
      file.gif    -> PICTURE
      file.webp   -> PICTURE
      file.avif   -> PICTURE
      file.apng   -> PICTURE
      file.ico    -> PICTURE
      file.bmp    -> PICTURE
      file.tiff   -> PICTURE
      file.tif    -> PICTURE
      file.heic   -> PICTURE
      file.svg    -> SVG
      file.mp4    -> VIDEO
      file.webm   -> VIDEO
      file.ogv    -> VIDEO
      file.avi    -> VIDEO
      file.mkv    -> VIDEO
      file.mov    -> VIDEO
      file.flv    -> VIDEO
      file.wmv    -> VIDEO
      file.mpeg   -> VIDEO
      file.mpg    -> VIDEO
      file.mp3    -> AUDIO
      file.wav    -> AUDIO
      file.ogg    -> AUDIO
      file.flac   -> AUDIO
      file.aac    -> AUDIO
      file.m4a    -> AUDIO
      file.wma    -> AUDIO
      file.opus   -> AUDIO
      file.pdf    -> PDF
      file.txt    -> TEXT
      file.log    -> TEXT
      file.ini    -> TEXT
      file.cfg    -> TEXT
      file.md     -> TEXT
      file.csv    -> TEXT
      file.json   -> TEXT
      file.yaml   -> TEXT
      file.yml    -> TEXT
      file.xml    -> TEXT
      file.toml   -> TEXT
      file.doc    -> OFFICE
      file.docx   -> OFFICE
      file.xls    -> OFFICE
      file.xlsx   -> OFFICE
      file.ppt    -> OFFICE
      file.pptx   -> OFFICE
      file.odt    -> OFFICE
      file.ods    -> OFFICE
      file.odp    -> OFFICE
      file.rtf    -> OFFICE
      file.zip    -> ARCHIVE
      file.rar    -> ARCHIVE
      file.7z     -> ARCHIVE
      file.tar    -> ARCHIVE
      file.gz     -> ARCHIVE
      file.bz2    -> ARCHIVE
      file.xz     -> ARCHIVE
      file.iso    -> ARCHIVE
      file.html   -> CODE
      file.htm    -> CODE
      file.css    -> CODE
      file.js     -> CODE
      file.ts     -> CODE
      file.tsx    -> CODE
      file.jsx    -> CODE
      file.java   -> CODE
      file.kt     -> CODE
      file.kts    -> CODE
      file.scala  -> CODE
      file.groovy -> CODE
      file.c      -> CODE
      file.cpp    -> CODE
      file.h      -> CODE
      file.hpp    -> CODE
      file.cs     -> CODE
      file.go     -> CODE
      file.rs     -> CODE
      file.swift  -> CODE
      file.dart   -> CODE
      file.py     -> CODE
      file.rb     -> CODE
      file.php    -> CODE
      file.sh     -> CODE
      file.bat    -> CODE
      file.ps1    -> CODE
      file.sql    -> CODE
      file.jsp    -> CODE
      file.vue    -> CODE
      file.svelte -> CODE
      file.ttf    -> FONT
      file.otf    -> FONT
      file.woff   -> FONT
      file.woff2  -> FONT
      file.eot    -> FONT
      file.obj    -> MODEL
      file.fbx    -> MODEL
      file.stl    -> MODEL
      file.gltf   -> MODEL
      file.glb    -> MODEL
      file.blend  -> MODEL
      file.3ds    -> MODEL
      file.dae    -> MODEL
      file.exe    -> EXECUTABLE
      file.msi    -> EXECUTABLE
      file.app    -> EXECUTABLE
      file.apk    -> EXECUTABLE
      file.jar    -> EXECUTABLE
      file.bin    -> EXECUTABLE
      file.db     -> DATA
      file.sqlite -> DATA
      file.mdb    -> DATA
      file.accdb  -> DATA
      file.parquet -> DATA
      file.avro   -> DATA
      .gitignore  -> OTHER
      placeholder -> OTHER
      """)
  void test(String fileName, FileType type) {
    assertThat(new FileTypeDetermination().get(fileName)).isEqualTo(type);
  }

  @Test
  void testNull() {
    assertThat(new FileTypeDetermination().get(null)).isEqualTo(OTHER);
  }

}