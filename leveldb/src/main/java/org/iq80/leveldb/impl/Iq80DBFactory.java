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
package org.iq80.leveldb.impl;

import android.util.Log;
import com.simsun.common.base.StandardCharsets;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.util.Closeables;
import org.iq80.leveldb.util.FileUtils;

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Iq80DBFactory implements DBFactory {

  public static final String TAG = "Iq80DBFactory";
  public interface TYPE_BYTES {
    int INT_BYTES = 32 / Byte.SIZE;
    int LONG_BYTES = 64 / Byte.SIZE;
    int FLOAT_BYTES = 32 / Byte.SIZE;
    int DOUBLE_BYTES = 64 / Byte.SIZE;
  }


  //// We only use MMAP on 64 bit systems since it's really easy to run out of
  //// virtual address space on a 32 bit system when all the data is getting mapped
  //// into memory.  If you really want to use MMAP anyways, use -Dleveldb.mmap=true
  public static final boolean USE_MMAP = false;
  public static final String VERSION = "1";
  public static final Iq80DBFactory factory = new Iq80DBFactory();

  public static byte[] bytes(String value) {
    return (value == null) ? null : value.getBytes(StandardCharsets.UTF_8);
  }

  public static byte[] bytes(int value) {
    return ByteBuffer.allocate(TYPE_BYTES.INT_BYTES).putInt(value).array();
  }

  public static byte[] bytes(long value) {
    return ByteBuffer.allocate(TYPE_BYTES.LONG_BYTES).putLong(value).array();
  }

  public static byte[] bytes(float value) {
    return ByteBuffer.allocate(TYPE_BYTES.FLOAT_BYTES).putFloat(value).array();
  }

  public static byte[] bytes(double value) {
    return ByteBuffer.allocate(TYPE_BYTES.DOUBLE_BYTES).putDouble(value).array();
  }

  public static byte[] bytes(boolean value) {
    return ByteBuffer.allocate(TYPE_BYTES.LONG_BYTES).put((byte) (value ? 1 : 0)).array();
  }

  public static String asString(byte[] value) {
    return (value == null) ? null : new String(value, StandardCharsets.UTF_8);
  }

  public static int asInt(byte[] value, int defValue) {
    if (value != null) {
      return ByteBuffer.wrap(value).getInt();
    } else {
      return defValue;
    }
  }

  public static long asLong(byte[] value, long defValue) {
    if (value != null) {
      return ByteBuffer.wrap(value).getLong();
    } else {
      return defValue;
    }
  }

  public static float asFloat(byte[] value, float defValue) {
    if (value != null) {
      return ByteBuffer.wrap(value).getFloat();
    } else {
      return defValue;
    }
  }

  public static double asDouble(byte[] value, double defValue) {
    if (value != null) {
      return ByteBuffer.wrap(value).getDouble();
    } else {
      return defValue;
    }
  }

  public static boolean asBoolean(byte[] value, boolean defValue) {
    if (value != null) {
      return ByteBuffer.wrap(value).getInt() != 0;
    } else {
      return defValue;
    }
  }

  @Override
  public DB open(File path, Options options) {
    try {
      return new DbImpl(options, path);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void destroy(File path, Options options) {
    // TODO: This should really only delete leveldb-created files.
    FileUtils.deleteRecursively(path);
  }

  @Override
  public void repair(File path, Options options) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return String.format("iq80 leveldb version %s", VERSION);
  }
}
