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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.util.Slice;
import org.iq80.leveldb.util.Slices;

import static com.simsun.common.base.Utils.requireNonNull;
import static org.iq80.leveldb.impl.Iq80DBFactory.asString;

public class WriteBatchImpl implements WriteBatch {
  private final List<Entry<Slice, Slice>> batch = new ArrayList<>();
  private int approximateSize;

  public int getApproximateSize() {
    return approximateSize;
  }

  public int size() {
    return batch.size();
  }

  @Override
  public WriteBatchImpl put(byte[] key, byte[] value) {
    requireNonNull(key, "key is null");
    requireNonNull(value, "value is null");
    batch.add(new AbstractMap.SimpleImmutableEntry<>(Slices.wrappedBuffer(key),
        Slices.wrappedBuffer(value)
    ));
    approximateSize += 12 + key.length + value.length;
    return this;
  }

  public WriteBatchImpl put(Slice key, Slice value) {
    requireNonNull(key, "key is null");
    requireNonNull(value, "value is null");
    batch.add(new AbstractMap.SimpleImmutableEntry<>(key, value));
    approximateSize += 12 + key.length() + value.length();
    return this;
  }

  @Override
  public WriteBatchImpl delete(byte[] key) {
    requireNonNull(key, "key is null");
    batch.add(new AbstractMap.SimpleImmutableEntry<>(Slices.wrappedBuffer(key), (Slice) null));
    approximateSize += 6 + key.length;
    return this;
  }

  @Override
  public List<String> getModifiedKeys() {
    List<String> list = new ArrayList<>();
    for (Entry<Slice, Slice> entry : batch) {
      String key = asString(entry.getKey().getBytes());
      list.add(key);
    }
    return list;
  }

  public WriteBatchImpl delete(Slice key) {
    requireNonNull(key, "key is null");
    batch.add(new AbstractMap.SimpleImmutableEntry<>(key, (Slice) null));
    approximateSize += 6 + key.length();
    return this;
  }

  @Override
  public void close() {
  }

  public void forEach(Handler handler) {
    for (Entry<Slice, Slice> entry : batch) {
      Slice key = entry.getKey();
      Slice value = entry.getValue();
      if (value != null) {
        handler.put(key, value);
      } else {
        handler.delete(key);
      }
    }
  }

  public interface Handler {
    void put(Slice key, Slice value);

    void delete(Slice key);
  }
}
