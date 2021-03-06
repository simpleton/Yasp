/*
 * Copyright (C) 2011 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iq80.leveldb.util;

import android.util.StringBuilderPrinter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.simsun.common.base.Preconditions.checkArgument;
import static com.simsun.common.base.Utils.requireNonNull;

public final class FileUtils {
  private static final int TEMP_DIR_ATTEMPTS = 10000;

  private FileUtils() {
  }

  public static boolean isSymbolicLink(File file) {
    try {
      File canonicalFile = file.getCanonicalFile();
      File absoluteFile = file.getAbsoluteFile();
      File parentFile = file.getParentFile();
      // a symbolic link has a different name between the canonical and absolute path
      return !canonicalFile.getName().equals(absoluteFile.getName()) ||
             // or the canonical parent path is not the same as the file's parent path,
             // provided the file has a parent path
             parentFile != null && !parentFile.getCanonicalPath().equals(canonicalFile.getParent());
    } catch (IOException e) {
      // error on the side of caution
      return true;
    }
  }

  public static List<File> listFiles(File dir) {
    File[] files = dir.listFiles();
    if (files == null) {
      return Collections.unmodifiableList(new ArrayList<File>());
    }
    return Collections.unmodifiableList(Arrays.asList(files));
  }

  public static List<File> listFiles(File dir, FilenameFilter filter) {
    File[] files = dir.listFiles(filter);
    if (files == null) {
      return Collections.unmodifiableList(new ArrayList<File>());
    }
    return Collections.unmodifiableList(Arrays.asList(files));
  }

  public static File createTempDir(String prefix) {
    return createTempDir(new File(System.getProperty("java.io.tmpdir")), prefix);
  }

  public static File createTempDir(File parentDir, String prefix) {
    String baseName = "";
    if (prefix != null) {
      baseName += prefix + "-";
    }

    baseName += System.currentTimeMillis() + "-";
    for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
      File tempDir = new File(parentDir, baseName + counter);
      if (tempDir.mkdir()) {
        return tempDir;
      }
    }
    throw new IllegalStateException("Failed to create directory within "
                                    + TEMP_DIR_ATTEMPTS
                                    + " attempts (tried "
                                    + baseName
                                    + "0 to "
                                    + baseName
                                    + (TEMP_DIR_ATTEMPTS - 1)
                                    + ')');
  }

  public static boolean deleteDirectoryContents(File directory) {
    checkArgument(directory.isDirectory(), "Not a directory: %s", directory);

    // Don't delete symbolic link directories
    if (isSymbolicLink(directory)) {
      return false;
    }

    boolean success = true;
    for (File file : listFiles(directory)) {
      success = deleteRecursively(file) && success;
    }
    return success;
  }

  public static boolean deleteRecursively(File file) {
    boolean success = true;
    if (file.isDirectory()) {
      success = deleteDirectoryContents(file);
    }

    return file.delete() && success;
  }

  public static boolean copyDirectoryContents(File src, File target) {
    checkArgument(src.isDirectory(), "Source dir is not a directory: %s", src);

    // Don't delete symbolic link directories
    if (isSymbolicLink(src)) {
      return false;
    }

    target.mkdirs();
    checkArgument(target.isDirectory(), "Target dir is not a directory: %s", src);

    boolean success = true;
    for (File file : listFiles(src)) {
      success = copyRecursively(file, new File(target, file.getName())) && success;
    }
    return success;
  }

  public static boolean copyRecursively(File src, File target) {
    if (src.isDirectory()) {
      return copyDirectoryContents(src, target);
    } else {
      try {
        copyFile(src, target);
        return true;
      } catch (IOException e) {
        return false;
      }
    }
  }

  public static File newFile(String parent, String... paths) {
    requireNonNull(parent, "parent is null");
    requireNonNull(paths, "paths is null");

    return newFile(new File(parent), paths);
  }

  public static File newFile(File parent, String... paths) {
    requireNonNull(parent, "parent is null");
    requireNonNull(paths, "paths is null");

    return newFile(parent, paths);
  }

  public static File newFile(File parent, Iterable<String> paths) {
    requireNonNull(parent, "parent is null");
    requireNonNull(paths, "paths is null");

    File result = parent;
    for (String path : paths) {
      result = new File(result, path);
    }
    return result;
  }

  private static void copyFile(File src, File target) throws IOException {
    InputStream in = new FileInputStream(src);
    OutputStream out = new FileOutputStream(target);
    try {
      byte[] buffer = new byte[4096];
      int count;
      while ((count = in.read(buffer)) != -1) {
        out.write(buffer, 0, count);
      }
    } finally {
      Closeables.closeQuietly(in);
      Closeables.closeQuietly(out);
    }
  }

  public static String readFile(File src, Charset charset) throws IOException {
    InputStream in = new FileInputStream(src);
    StringBuilder sb = new StringBuilder();
    try {
      byte[] buffer = new byte[4096];
      int count;
      while ((count = in.read(buffer)) != -1) {
        sb.append(new String(buffer, 0, count));
      }
    } finally {
      Closeables.closeQuietly(in);
    }
    return sb.toString();
  }
}
