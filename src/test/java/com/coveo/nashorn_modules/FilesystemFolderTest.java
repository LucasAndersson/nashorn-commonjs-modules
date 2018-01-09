package com.coveo.nashorn_modules;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.script.ScriptEngineManager;

import jdk.nashorn.api.scripting.NashornScriptEngine;

import static org.junit.Assert.*;

public class FilesystemFolderTest {
  private File file = new File("src/test/resources/com/coveo/nashorn_modules/test1");
  private FilesystemFolder root = FilesystemFolder.create(file, "UTF-8");

  private File subfile = new File(file, "subdir");
  private File subsubfile = new File(subfile, "subsubdir");

  private String rootPath =
      file.getAbsolutePath().substring(0, file.getAbsolutePath().indexOf(File.separator));

  @Test
  public void usesTimestampForValidation() throws IOException {
    Path tempDirectory = Files.createTempDirectory("foo");
    FilesystemFolder target =
        FilesystemFolder.createWithValidation(tempDirectory.toFile(), "UTF-8");
    File file = new File(tempDirectory.toFile(), "foo.js");
    boolean created = file.createNewFile();
    assertTrue(created);
    String cacheValidatorBefore = target.getCacheValidator("foo.js");
    assertNotNull(cacheValidatorBefore);
    boolean modified = file.setLastModified(file.lastModified() + 5000);
    assertTrue(modified);
    String cacheValidatorAfter = target.getCacheValidator("foo.js");
    assertNotEquals(cacheValidatorBefore, cacheValidatorAfter);
  }

  @Test
  public void rootFolderHasTheExpectedProperties() {
    assertTrue(root.getPath().startsWith(rootPath));
    assertTrue(root.getPath().endsWith(file.getPath() + File.separator));
    assertNull(root.getParent());
  }

  @Test
  public void getFileReturnsTheContentOfTheFileWhenItExists() {
    assertTrue(root.getFile("foo.js").getString().contains("foo"));
  }

  @Test
  public void getFileReturnsNullWhenFileDoesNotExists() {
    assertNull(root.getFile("invalid"));
  }

  @Test
  public void getFolderReturnsAnObjectWithTheExpectedProperties() {
    Folder sub = root.getFolder("subdir");
    assertTrue(sub.getPath().startsWith(rootPath));
    assertTrue(sub.getPath().endsWith(subfile.getPath() + File.separator));
    assertSame(root, sub.getParent());
    Folder subsub = sub.getFolder("subsubdir");
    assertTrue(subsub.getPath().startsWith(rootPath));
    assertTrue(subsub.getPath().endsWith(subsubfile.getPath() + File.separator));
    assertSame(sub, subsub.getParent());
  }

  @Test
  public void getFolderReturnsNullWhenFolderDoesNotExist() {
    assertNull(root.getFolder("invalid"));
  }

  @Test
  public void getFileCanBeUsedOnSubFolderIfFileExist() {
    assertTrue(root.getFolder("subdir").getFile("bar.js").getString().contains("bar"));
  }

  @Test
  public void filesystemFolderWorksWhenUsedForReal() throws Throwable {
    NashornScriptEngine engine =
        (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
    Require.enable(engine, root);
    assertEquals("spam", engine.eval("require('./foo').bar.spam.spam"));
  }
}
